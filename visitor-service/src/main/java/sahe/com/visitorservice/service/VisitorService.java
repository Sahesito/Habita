package sahe.com.visitorservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sahe.com.visitorservice.domain.entity.Visitor;
import sahe.com.visitorservice.domain.entity.VisitorAccessLog;
import sahe.com.visitorservice.dto.request.CheckinRequest;
import sahe.com.visitorservice.dto.request.CreateVisitorRequest;
import sahe.com.visitorservice.dto.response.VisitorResponse;
import sahe.com.visitorservice.exception.HabitaException;
import sahe.com.visitorservice.repository.VisitorAccessLogRepository;
import sahe.com.visitorservice.repository.VisitorRepository;
import sahe.com.visitorservice.util.AccessCodeGenerator;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final VisitorAccessLogRepository accessLogRepository;
    private final AccessCodeGenerator accessCodeGenerator;

    @Transactional
    public VisitorResponse registerVisitor(
            CreateVisitorRequest request,
            String tenantId,
            String hostResidentId,
            String hostEmail,
            String hostPhone,
            String apartmentLabel) {

        log.info("Registering visitor: {} for host: {}", request.getVisitorName(), hostResidentId);

        String accessCode = generateUniqueAccessCode();

        Visitor visitor = Visitor.builder()
                .tenantId(tenantId)
                .visitorName(request.getVisitorName())
                .visitorDocument(request.getVisitorDocument())
                .visitorPhone(request.getVisitorPhone())
                .hostResidentId(hostResidentId)
                .hostEmail(hostEmail)
                .hostPhone(hostPhone)
                .apartmentLabel(apartmentLabel)
                .accessCode(accessCode)
                .expectedDate(request.getExpectedDate())
                .expectedTimeFrom(request.getExpectedTimeFrom())
                .expectedTimeTo(request.getExpectedTimeTo())
                .purpose(request.getPurpose())
                .notes(request.getNotes())
                .status("EXPECTED")
                .build();

        visitor = visitorRepository.save(visitor);

        logAction(visitor, "REGISTERED", hostResidentId, null);

        log.info("Visitor registered: {} with access code: {}", visitor.getId(), accessCode);

        return mapToResponse(visitor);
    }

    @Transactional
    public VisitorResponse checkin(CheckinRequest request, String guardId) {
        log.info("Check-in attempt with code: {}", request.getAccessCode());

        Visitor visitor = visitorRepository.findByAccessCode(request.getAccessCode())
                .orElseThrow(() -> HabitaException.notFound("Invalid access code"));

        if (visitor.isInside()) {
            throw HabitaException.conflict("Visitor is already inside");
        }

        if ("CANCELLED".equals(visitor.getStatus())) {
            throw HabitaException.badRequest("This visit has been cancelled");
        }

        visitor.setStatus("INSIDE");
        visitor.setCheckinAt(Instant.now());
        visitor.setCheckinGuardId(guardId);
        visitor.setVehiclePlate(request.getVehiclePlate());

        visitor = visitorRepository.save(visitor);

        logAction(visitor, "CHECKED_IN", guardId, null);

        log.info("Visitor checked in: {}", visitor.getId());

        return mapToResponse(visitor);
    }

    @Transactional
    public VisitorResponse checkout(UUID visitorId, String guardId, String notes) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> HabitaException.notFound("Visitor not found: " + visitorId));

        if (!visitor.isInside()) {
            throw HabitaException.badRequest("Visitor is not currently inside");
        }

        visitor.setStatus("LEFT");
        visitor.setCheckoutAt(Instant.now());
        visitor.setCheckoutGuardId(guardId);

        visitor = visitorRepository.save(visitor);

        logAction(visitor, "CHECKED_OUT", guardId, notes);

        log.info("Visitor checked out: {}", visitor.getId());

        return mapToResponse(visitor);
    }

    public VisitorResponse getVisitorById(UUID id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> HabitaException.notFound("Visitor not found: " + id));
        return mapToResponse(visitor);
    }

    public List<VisitorResponse> getActiveVisitors(String tenantId) {
        return visitorRepository.findByTenantIdAndStatus(tenantId, "INSIDE").stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Page<VisitorResponse> getMyVisitors(String tenantId, String hostResidentId, Pageable pageable) {
        return visitorRepository.findByTenantIdAndHostResidentId(tenantId, hostResidentId, pageable)
                .map(this::mapToResponse);
    }

    public Page<VisitorResponse> getHistory(String tenantId, Instant from, Instant to, Pageable pageable) {
        return visitorRepository.findHistory(tenantId, from, to, pageable)
                .map(this::mapToResponse);
    }

    private String generateUniqueAccessCode() {
        String code;
        int attempts = 0;
        do {
            code = accessCodeGenerator.generate();
            attempts++;
            if (attempts > 10) {
                throw new RuntimeException("Could not generate a unique access code");
            }
        } while (visitorRepository.existsByAccessCode(code));
        return code;
    }

    private void logAction(Visitor visitor, String action, String performedBy, String notes) {
        VisitorAccessLog log = VisitorAccessLog.builder()
                .visitor(visitor)
                .tenantId(visitor.getTenantId())
                .action(action)
                .performedBy(performedBy)
                .notes(notes)
                .build();
        accessLogRepository.save(log);
    }

    private VisitorResponse mapToResponse(Visitor v) {
        return VisitorResponse.builder()
                .id(v.getId().toString())
                .visitorName(v.getVisitorName())
                .host(VisitorResponse.HostInfo.builder()
                        .id(v.getHostResidentId())
                        .apartment(v.getApartmentLabel())
                        .phone(v.getHostPhone())
                        .build())
                .accessCode(v.getAccessCode())
                .qrUrl("https://habita.io/qr/" + v.getAccessCode())
                .expectedDate(v.getExpectedDate())
                .expectedTimeFrom(v.getExpectedTimeFrom())
                .expectedTimeTo(v.getExpectedTimeTo())
                .status(v.getStatus())
                .checkinAt(v.getCheckinAt())
                .checkoutAt(v.getCheckoutAt())
                .vehiclePlate(v.getVehiclePlate())
                .createdAt(v.getCreatedAt())
                .build();
    }
}