package sahe.com.reservationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sahe.com.reservationservice.dto.request.CreateAreaRequest;
import sahe.com.reservationservice.dto.response.AreaResponse;
import sahe.com.reservationservice.service.CommonAreaService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
public class CommonAreaController {

    private final CommonAreaService commonAreaService;

    @PostMapping
    public ResponseEntity<AreaResponse> createArea(
            @Valid @RequestBody CreateAreaRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        AreaResponse response = commonAreaService.createArea(request, tenantId);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AreaResponse>> getAreas(
            @RequestHeader("X-Tenant-Id") String tenantId) {

        return ResponseEntity.ok(commonAreaService.getAreas(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaResponse> getAreaById(@PathVariable UUID id) {
        return ResponseEntity.ok(commonAreaService.getAreaById(id));
    }
}