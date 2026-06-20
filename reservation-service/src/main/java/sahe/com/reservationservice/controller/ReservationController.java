package sahe.com.reservationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sahe.com.reservationservice.dto.request.CancelReservationRequest;
import sahe.com.reservationservice.dto.request.CreateReservationRequest;
import sahe.com.reservationservice.dto.response.ReservationResponse;
import sahe.com.reservationservice.service.ReservationService;

import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader("X-Resident-Id") String residentId,
            @RequestHeader(value = "X-Resident-Email", required = false) String residentEmail,
            @RequestHeader(value = "X-Resident-Name", required = false) String residentName,
            @RequestHeader(value = "X-Apartment-Label", required = false) String apartmentLabel) {

        ReservationResponse response = reservationService.createReservation(
                request, tenantId, residentId, residentEmail, residentName, apartmentLabel
        );
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ReservationResponse>> getMyReservations(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader("X-Resident-Id") String residentId,
            Pageable pageable) {

        return ResponseEntity.ok(reservationService.getMyReservations(tenantId, residentId, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            @RequestHeader("X-Tenant-Id") String tenantId,
            Pageable pageable) {

        return ResponseEntity.ok(reservationService.getAllReservations(tenantId, pageable));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable UUID id,
            @RequestBody CancelReservationRequest request,
            @RequestHeader("X-Resident-Id") String residentId) {

        reservationService.cancelReservation(id, residentId, request.getReason());
        return ResponseEntity.noContent().build();
    }
}