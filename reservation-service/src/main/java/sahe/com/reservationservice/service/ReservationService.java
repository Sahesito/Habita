package sahe.com.reservationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sahe.com.reservationservice.domain.entity.CommonArea;
import sahe.com.reservationservice.domain.entity.Reservation;
import sahe.com.reservationservice.dto.request.CreateReservationRequest;
import sahe.com.reservationservice.dto.response.ReservationResponse;
import sahe.com.reservationservice.exception.HabitaException;
import sahe.com.reservationservice.repository.CommonAreaRepository;
import sahe.com.reservationservice.repository.ReservationRepository;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CommonAreaRepository commonAreaRepository;

    @Transactional
    public ReservationResponse createReservation(
            CreateReservationRequest request,
            String tenantId,
            String residentId,
            String residentEmail,
            String residentName,
            String apartmentLabel) {

        log.info("Creating reservation for resident: {} area: {}", residentId, request.getAreaId());

        UUID areaId = UUID.fromString(request.getAreaId());
        CommonArea area = commonAreaRepository.findById(areaId)
                .orElseThrow(() -> HabitaException.notFound("Common area not found: " + areaId));

        if (!"ACTIVE".equals(area.getStatus())) {
            throw HabitaException.badRequest("This area is not available for reservations");
        }

        if (request.getStartTime().isAfter(request.getEndTime()) ||
                request.getStartTime().equals(request.getEndTime())) {
            throw HabitaException.badRequest("Start time must be before end time");
        }

        // Critical: conflict detection using DB-level locking via the query
        long conflicts = reservationRepository.countConflicts(
                areaId, request.getDate(), request.getStartTime(), request.getEndTime()
        );

        if (conflicts > 0) {
            throw HabitaException.conflict("The area is already reserved during that time slot");
        }

        Reservation reservation = Reservation.builder()
                .tenantId(tenantId)
                .area(area)
                .residentId(residentId)
                .residentEmail(residentEmail)
                .residentName(residentName)
                .apartmentLabel(apartmentLabel)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .attendeesCount(request.getAttendeesCount())
                .notes(request.getNotes())
                .status("CONFIRMED")
                .build();

        reservation = reservationRepository.save(reservation);

        log.info("Reservation created: {}", reservation.getId());

        return mapToResponse(reservation);
    }

    public ReservationResponse getReservationById(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> HabitaException.notFound("Reservation not found: " + id));
        return mapToResponse(reservation);
    }

    public Page<ReservationResponse> getMyReservations(String tenantId, String residentId, Pageable pageable) {
        return reservationRepository.findByTenantIdAndResidentId(tenantId, residentId, pageable)
                .map(this::mapToResponse);
    }

    public Page<ReservationResponse> getAllReservations(String tenantId, Pageable pageable) {
        return reservationRepository.findByTenantId(tenantId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void cancelReservation(UUID id, String cancelledBy, String reason) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> HabitaException.notFound("Reservation not found: " + id));

        if ("CANCELLED".equals(reservation.getStatus())) {
            throw HabitaException.badRequest("Reservation is already cancelled");
        }

        reservation.setStatus("CANCELLED");
        reservation.setCancelledAt(Instant.now());
        reservation.setCancelledBy(cancelledBy);
        reservation.setCancelReason(reason);

        reservationRepository.save(reservation);

        log.info("Reservation cancelled: {} by {}", id, cancelledBy);
    }

    private ReservationResponse mapToResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId().toString())
                .area(ReservationResponse.AreaInfo.builder()
                        .id(r.getArea().getId().toString())
                        .name(r.getArea().getName())
                        .capacity(r.getArea().getCapacity())
                        .build())
                .resident(ReservationResponse.ResidentInfo.builder()
                        .id(r.getResidentId())
                        .fullName(r.getResidentName())
                        .apartment(r.getApartmentLabel())
                        .build())
                .date(r.getDate())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .attendeesCount(r.getAttendeesCount())
                .notes(r.getNotes())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .build();
    }
}