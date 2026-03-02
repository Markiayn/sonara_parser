package ua.markiyan.sonara_parser.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper; // Виправив імпорт
import org.springframework.stereotype.Service;
import ua.markiyan.sonara_parser.dto.response.LastFmTrackResponse;
import ua.markiyan.sonara_parser.entity.Descriptions;
import ua.markiyan.sonara_parser.entity.Statistics;
import ua.markiyan.sonara_parser.entity.Track;
import ua.markiyan.sonara_parser.entity.enums.OwnerType;
import ua.markiyan.sonara_parser.repository.DescriptionsRepository;
import ua.markiyan.sonara_parser.repository.StatisticsRepository;
import ua.markiyan.sonara_parser.repository.TrackRepository;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder; // Для безпечного кодування URL
import java.net.http.HttpClient;
import java.net.http.HttpRequest; // Виправив імпорт
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserService {

    private final StatisticsRepository statsRepo;
    private final DescriptionsRepository descRepo;
    private final TrackRepository trackRepo;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .build();

    private final String API_KEY = "a53fac334080c0c3ec827ace10eac7d8";

    public void fetchAndSaveTrack(Long trackId, String artist, String title) {
        // Використовуємо віртуальний потік для всього процесу
        Thread.ofVirtual().start(() -> {
            try {
                String encodedArtist = URLEncoder.encode(artist, StandardCharsets.UTF_8);
                String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

                String url = String.format(
                        "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=%s&artist=%s&track=%s&format=json",
                        API_KEY, encodedArtist, encodedTitle
                );

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

                // HttpClient автоматично використає віртуальний потік для очікування відповіді
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                LastFmTrackResponse data = objectMapper.readValue(response.body(), LastFmTrackResponse.class);

                if (data.getTrack() != null) {
                    saveData(trackId, data.getTrack());
                }
            } catch (Exception e) {
                System.err.println("Помилка для " + title + ": " + e.getMessage());
            }
        });
    }

    @Transactional // Тепер метод public, щоб транзакція працювала
    public void saveData(Long trackId, LastFmTrackResponse.TrackData info) {
        // Зберігаємо статистику (playcount, listeners)
        Statistics stats = statsRepo.findByOwnerIdAndOwnerType(trackId, OwnerType.TRACK)
                .orElse(new Statistics());
        stats.setOwnerId(trackId);
        stats.setOwnerType(OwnerType.TRACK);
        stats.setListenersCount(Long.parseLong(info.getListeners()));
        stats.setPlayCount(Long.parseLong(info.getPlaycount()));
        statsRepo.save(stats);

        // Зберігаємо описи (wiki)
        if (info.getWiki() != null) {
            Descriptions desc = descRepo.findByOwnerIdAndOwnerType(trackId, OwnerType.TRACK)
                    .orElse(new Descriptions());
            desc.setOwnerId(trackId);
            desc.setOwnerType(OwnerType.TRACK);
            desc.setSummary(info.getWiki().getSummary());
            desc.setFullContent(info.getWiki().getContent());
            descRepo.save(desc);
        }
    }

    @Transactional
    public void updateAllMusicData() {
        List<Track> tracks = trackRepo.findAll();
        log.info("🔎 Знайдено треків: {}. Починаємо парсинг...", tracks.size());

        for (Track track : tracks) {
            String artistName = track.getArtist().getName();
            String trackTitle = track.getTitle();

            fetchAndSaveTrack(track.getId(), artistName, trackTitle);

            try {
                // Робимо паузу 100мс між запуском потоків,
                // щоб не "покласти" API Last.fm
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}