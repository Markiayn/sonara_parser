package ua.markiyan.sonara_parser;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ua.markiyan.TrackRequest;
import ua.markiyan.TrackResponse;
import ua.markiyan.TrackServiceGrpc;

// Анотація автоматично реєструє цей сервіс на порту 9090 (за замовчуванням)
@GrpcService
public class TrackDataServiceImpl extends TrackServiceGrpc.TrackServiceImplBase {

    @Override
    public void getTrackInfo(TrackRequest request, StreamObserver<TrackResponse> responseObserver) {
        // Тут ми імітуємо парсинг. В майбутньому тут буде логіка збору даних з мережі.
        System.out.println("Отримано запит для треку з ID: " + request.getId());

        TrackResponse response = TrackResponse.newBuilder()
                .setTitle("Назва треку для " + request.getId())
                .setDescription("Це детальний опис, який ми нібито щойно спарсили з інтернету.")
                .build();

        // Відправляємо відповідь клієнту
        responseObserver.onNext(response);
        // Кажемо, що ми закінчили
        responseObserver.onCompleted();
    }
}