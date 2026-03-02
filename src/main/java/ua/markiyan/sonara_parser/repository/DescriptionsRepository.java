package ua.markiyan.sonara_parser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.markiyan.sonara_parser.entity.Descriptions;
import ua.markiyan.sonara_parser.entity.enums.OwnerType;

import java.util.Optional;

public interface DescriptionsRepository extends JpaRepository<Descriptions, Long> {
    Optional<Descriptions> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
}
