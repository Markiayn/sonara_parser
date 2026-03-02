package ua.markiyan.sonara_parser.entity;

import jakarta.persistence.*;
import lombok.Data;
import ua.markiyan.sonara_parser.entity.enums.OwnerType;

@Entity
@Table(name = "descriptions")
@Data
public class Descriptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 10)
    private OwnerType ownerType;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "full_content", columnDefinition = "LONGTEXT")
    private String fullContent;
}