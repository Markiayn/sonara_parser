package ua.markiyan.sonara_parser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.markiyan.sonara_parser.entity.Statistics;
import ua.markiyan.sonara_parser.entity.enums.OwnerType;

import java.util.Optional;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    Optional<Statistics> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
}
