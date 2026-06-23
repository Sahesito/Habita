package sahe.com.visitorservice.repository;

import sahe.com.visitorservice.domain.entity.VisitorAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VisitorAccessLogRepository extends JpaRepository<VisitorAccessLog, UUID> {

    List<VisitorAccessLog> findByVisitorId(UUID visitorId);
}