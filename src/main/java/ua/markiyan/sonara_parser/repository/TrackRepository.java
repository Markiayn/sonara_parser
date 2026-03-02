package ua.markiyan.sonara_parser.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.markiyan.sonara_parser.entity.Track; // Переконайся, що шлях до Entity правильний

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    @Override
    @EntityGraph(attributePaths = {"artist"})
    List<Track> findAll();
}