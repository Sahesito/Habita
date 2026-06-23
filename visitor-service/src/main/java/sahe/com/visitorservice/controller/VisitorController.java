package sahe.com.visitorservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sahe.com.visitorservice.dto.request.CheckinRequest;
import sahe.com.visitorservice.dto.request.CheckoutRequest;
import sahe.com.visitorservice.dto.request.CreateVisitorRequest;
import sahe.com.visitorservice.dto.response.VisitorResponse;
import sahe.com.visitorservice.service.VisitorService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/visitors")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping
    public ResponseEntity<VisitorResponse> registerVisitor(
            @Valid @RequestBody CreateVisitorRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader("X-Resident-Id") String hostResidentId,
            @RequestHeader(value = "X-Resident-Email", required = false) String hostEmail,
            @RequestHeader(value = "X-Resident-Phone", required = false) String hostPhone,
            @RequestHeader(value = "X-Apartment-Label", required = false) String apartmentLabel) {

        VisitorResponse response = visitorService.registerVisitor(
                request, tenantId, hostResidentId, hostEmail, hostPhone, apartmentLabel
        );
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorResponse> getVisitorById(@PathVariable UUID id) {
        return ResponseEntity.ok(visitorService.getVisitorById(id));
    }

    @PutMapping("/{id}/checkin")
    public ResponseEntity<VisitorResponse> checkin(
            @Valid @RequestBody CheckinRequest request,
            @RequestHeader("X-Guard-Id") String guardId) {

        return ResponseEntity.ok(visitorService.checkin(request, guardId));
    }

    @PutMapping("/{id}/checkout")
    public ResponseEntity<VisitorResponse> checkout(
            @PathVariable UUID id,
            @RequestBody CheckoutRequest request,
            @RequestHeader("X-Guard-Id") String guardId) {

        return ResponseEntity.ok(visitorService.checkout(id, guardId, request.getNotes()));
    }

    @GetMapping("/active")
    public ResponseEntity<List<VisitorResponse>> getActiveVisitors(
            @RequestHeader("X-Tenant-Id") String tenantId) {

        return ResponseEntity.ok(visitorService.getActiveVisitors(tenantId));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<VisitorResponse>> getMyVisitors(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestHeader("X-Resident-Id") String hostResidentId,
            Pageable pageable) {

        return ResponseEntity.ok(visitorService.getMyVisitors(tenantId, hostResidentId, pageable));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<VisitorResponse>> getHistory(
            @RequestHeader("X-Tenant-Id") String tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            Pageable pageable) {

        return ResponseEntity.ok(visitorService.getHistory(tenantId, from, to, pageable));
    }
}