package ua.markiyan.sonara_parser.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import ua.markiyan.sonara_parser.entity.enums.OwnerType;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "statistics")

public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId; // ID з таблиці tracks або artists

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 10)
    private OwnerType ownerType;

    @Column(name = "listeners_count")
    private Long listenersCount;

    @Column(name = "play_count")
    private Long playCount;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
