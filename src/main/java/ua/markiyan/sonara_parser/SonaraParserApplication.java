package ua.markiyan.sonara_parser;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import ua.markiyan.sonara_parser.service.DbLoggingService;
import ua.markiyan.sonara_parser.service.ParserService;

@Slf4j // Додали для дублювання логів у консоль
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SonaraParserApplication {

    // Вказуємо окремий модуль, щоб в БД було видно, що це логи зі старту програми
    private static final String MODULE_NAME = "APP_PARSER_RUNNER";

    public static void main(String[] args) {
        SpringApplication.run(SonaraParserApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    // Інжектимо DbLoggingService прямо в параметри!
    public CommandLineRunner testRun(ParserService parserService, DbLoggingService dbLogger) {
        return args -> {
            String startMsg = "Починаємо масове оновлення даних для всієї медіатеки при старті додатка...";

            log.info("🚀 {}", startMsg); // Красиво в консоль (за правилами SLF4J)
            dbLogger.logInfo(MODULE_NAME, startMsg); // Надійно в базу даних

            // Викликаємо метод, який дістає всі треки з БД і парсить Last.fm
            parserService.updateAllMusicData();

            String endMsg = "Процес успішно делеговано у віртуальні потоки. Перевіряй базу system_logs!";

            log.info("✅ {}", endMsg);
            dbLogger.logInfo(MODULE_NAME, endMsg);
        };
    }
}
