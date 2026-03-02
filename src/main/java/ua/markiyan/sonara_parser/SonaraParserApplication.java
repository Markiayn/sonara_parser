package ua.markiyan.sonara_parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Ось цей імпорт
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import ua.markiyan.sonara_parser.service.ParserService;

@SpringBootApplication
@EnableScheduling
public class SonaraParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(SonaraParserApplication.class, args);
	}

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

//    @Bean
//    public CommandLineRunner testRun(ParserService parserService) {
//        return args -> {
//            System.out.println("🚀 Починаємо масове оновлення даних для всієї медіатеки...");
//
//            // Викликаємо метод, який дістає всі треки з БД і парсить Last.fm
//            parserService.updateAllMusicData();
//
//            System.out.println("✅ Процес запущено у віртуальних потоках. Перевіряй базу!");
//        };
//    }
}
