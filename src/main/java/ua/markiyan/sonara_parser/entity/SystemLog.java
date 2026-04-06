package ua.markiyan.sonara_parser.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "system_logs")
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "log_level", nullable = false, length = 10)
    private String level; // INFO, WARN, ERROR

    @Column(name = "module", length = 50)
    private String module; // Наприклад: "PARSER", "AUTH", "TRACK_SERVICE"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(columnDefinition = "TEXT")
    private String stackTrace; // Якщо це помилка, сюди запишемо деталі

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
