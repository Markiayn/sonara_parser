package ua.markiyan.sonara_parser.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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

    // 1. Інжектимо наш кастомний сервіс логування
    private final DbLoggingService dbLogger;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .executor(Executors.newVirtualThreadPerTaskExecutor())
            .build();

    private final String API_KEY = "a53fac334080c0c3ec827ace10eac7d8";
    private static final String MODULE_NAME = "LAST_FM_PARSER"; // Константа для модуля логів

    public void fetchAndSaveTrack(Long trackId, String artist, String title) {
        Thread.ofVirtual().start(() -> {
            try {
                String encodedArtist = URLEncoder.encode(artist, StandardCharsets.UTF_8);
                String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

                String url = String.format(
                        "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=%s&artist=%s&track=%s&format=json",
                        API_KEY, encodedArtist, encodedTitle
                );

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                LastFmTrackResponse data = objectMapper.readValue(response.body(), LastFmTrackResponse.class);

                if (data.getTrack() != null) {
                    saveData(trackId, data.getTrack());

                    // 2. Логуємо успішний парсинг одного треку (зберігаємо гарний формат повідомлення)
                    String msg = String.format("Успішно оновлено статистику для треку ID: %d (%s - %s)", trackId, artist, title);
                    dbLogger.logInfo(MODULE_NAME, msg);
                } else {
                    // Логуємо випадок, коли API не знайшло такий трек
                    dbLogger.logInfo(MODULE_NAME, "Last.fm не знайшов даних для треку: " + artist + " - " + title);
                }
            } catch (Exception e) {
                // 3. Замінили System.err.println на повноцінний запис помилки в БД!
                // Передаємо Exception 'e' останнім параметром, як вчила документація Log4j
                String errorMsg = String.format("Помилка парсингу треку ID: %d (%s - %s)", trackId, artist, title);
                dbLogger.logError(MODULE_NAME, errorMsg, e);

                // Також дублюємо в консоль для швидкого дебагу під час розробки
                log.error("Помилка для треку {}: {}", title, e.getMessage(), e);
            }
        });
    }

    @Transactional
    public void saveData(Long trackId, LastFmTrackResponse.TrackData info) {
        Statistics stats = statsRepo.findByOwnerIdAndOwnerType(trackId, OwnerType.TRACK)
                .orElse(new Statistics());
        stats.setOwnerId(trackId);
        stats.setOwnerType(OwnerType.TRACK);

        // Додана мінімальна перевірка на null, щоб не вилітав NullPointerException
        long listeners = info.getListeners() != null ? Long.parseLong(info.getListeners()) : 0L;
        long playCount = info.getPlaycount() != null ? Long.parseLong(info.getPlaycount()) : 0L;

        stats.setListenersCount(listeners);
        stats.setPlayCount(playCount);
        statsRepo.save(stats);

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

        // 4. Логуємо старт глобального процесу
        String startMsg = String.format("Запуск масового парсингу. Знайдено треків для обробки: %d", tracks.size());
        dbLogger.logInfo(MODULE_NAME, startMsg);
        log.info(startMsg); // Дублюємо в консоль

        for (Track track : tracks) {
            String artistName = track.getArtist().getName();
            String trackTitle = track.getTitle();

            fetchAndSaveTrack(track.getId(), artistName, trackTitle);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                dbLogger.logError(MODULE_NAME, "Процес парсингу був перерваний (InterruptedException)", e);
            }
        }
    }
}