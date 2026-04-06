package ua.markiyan.sonara_parser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.markiyan.sonara_parser.entity.SystemLog;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
}
