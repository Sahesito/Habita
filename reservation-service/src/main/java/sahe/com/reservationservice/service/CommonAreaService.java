package sahe.com.reservationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sahe.com.reservationservice.domain.entity.CommonArea;
import sahe.com.reservationservice.dto.request.CreateAreaRequest;
import sahe.com.reservationservice.dto.response.AreaResponse;
import sahe.com.reservationservice.exception.HabitaException;
import sahe.com.reservationservice.repository.CommonAreaRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonAreaService {

    private final CommonAreaRepository commonAreaRepository;

    @Transactional
    public AreaResponse createArea(CreateAreaRequest request, String tenantId) {
        CommonArea area = CommonArea.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .location(request.getLocation())
                .rules(request.getRules())
                .status("ACTIVE")
                .build();

        area = commonAreaRepository.save(area);
        log.info("Common area created: {}", area.getId());

        return mapToResponse(area);
    }

    public List<AreaResponse> getAreas(String tenantId) {
        return commonAreaRepository.findByTenantId(tenantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AreaResponse getAreaById(UUID id) {
        CommonArea area = commonAreaRepository.findById(id)
                .orElseThrow(() -> HabitaException.notFound("Common area not found: " + id));
        return mapToResponse(area);
    }

    private AreaResponse mapToResponse(CommonArea area) {
        return AreaResponse.builder()
                .id(area.getId().toString())
                .name(area.getName())
                .description(area.getDescription())
                .capacity(area.getCapacity())
                .location(area.getLocation())
                .status(area.getStatus())
                .build();
    }
}