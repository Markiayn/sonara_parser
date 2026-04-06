package ua.markiyan.sonara_parser.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.markiyan.sonara_parser.entity.SystemLog;
import ua.markiyan.sonara_parser.repository.SystemLogRepository;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class DbLoggingService {

    private final SystemLogRepository logRepository;

    @Async // Виконується в окремому потоці!
    @Transactional
    public void logInfo(String module, String message) {
        SystemLog log = SystemLog.builder()
                .level("INFO")
                .module(module)
                .message(message)
                .build();
        logRepository.save(log);
    }

    @Async
    @Transactional
    public void logError(String module, String message, Throwable exception) {
        SystemLog log = SystemLog.builder()
                .level("ERROR")
                .module(module)
                .message(message)
                .stackTrace(getStackTraceAsString(exception)) // Зберігаємо весь стек помилки
                .build();
        logRepository.save(log);
    }

    // Утиліта для перетворення Exception у звичайний String
    private String getStackTraceAsString(Throwable e) {
        if (e == null) return null;
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
