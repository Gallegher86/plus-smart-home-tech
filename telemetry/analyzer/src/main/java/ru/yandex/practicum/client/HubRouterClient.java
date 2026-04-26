package ru.yandex.practicum.client;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;

@Service
@RequiredArgsConstructor
public class HubRouterClient {

    @GrpcClient("hub-router")
    private final HubRouterControllerBlockingStub client;

    public void send(DeviceActionRequest request) {
        client.handleDeviceAction(request);
    }
}
