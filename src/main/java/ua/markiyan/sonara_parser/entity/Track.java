package ua.markiyan.sonara_parser.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(
        name = "Track",
        indexes = {
                @Index(name = "idx_track_album", columnList = "album_id"),
                @Index(name = "idx_track_artist", columnList = "artist_id")
        }
)
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "duration_sec", nullable = false)
    private Integer durationSec;

    @Column(name = "audio_key", length = 255)
    private String audioKey;

    @Column(name = "explicit_flag", nullable = false)
    private boolean explicitFlag = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(name = "audio_url", nullable = false)
    private String audioUrl;
}

