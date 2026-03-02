package ua.markiyan.sonara_parser.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.markiyan.sonara_parser.service.ParserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class MusicDataScheduler {

    private final ParserService parserService;

    // "0 0 0,12 * * *" — означає 0-ва секунда, 0-ва хвилина,
    // години 0 (північ) та 12 (полудень) кожного дня.
    @Scheduled(cron = "0 0 0,12 * * *")
    public void scheduleTwiceADayUpdate() {
        log.info("⏰ Час оновлення! (00:00/12:00). Запускаємо парсинг усієї медіатеки...");

        try {
            parserService.updateAllMusicData();
            log.info("🚀 Процес масового оновлення успішно ініційовано у віртуальних потоках.");
        } catch (Exception e) {
            log.error("❌ Помилка під час запуску шедулера: {}", e.getMessage());
        }
    }
}