package com.algaworks.algadelivery.courier.management.infrastructure.kafka;

import com.algaworks.algadelivery.courier.management.domain.service.CourierDeliveryService;
import com.algaworks.algadelivery.courier.management.infrastructure.event.DeliveryFulfilledIntegrationEvent;
import com.algaworks.algadelivery.courier.management.infrastructure.event.DeliveryPlacedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class KafkaDeliveriesMessageHandlerTest {

    @Mock
    private CourierDeliveryService courierDeliveryService;

    @InjectMocks
    private KafkaDeliveriesMessageHandler kafkaDeliveriesMessageHandler;

    @Test
    void shouldAssignDeliveryWhenDeliveryPlacedEventIsReceived() {
        UUID deliveryId = UUID.randomUUID();

        kafkaDeliveriesMessageHandler.handle(
                new DeliveryPlacedIntegrationEvent(OffsetDateTime.now(), deliveryId));

        verify(courierDeliveryService).assignDelivery(deliveryId);
    }

    @Test
    void shouldFulfillDeliveryWhenDeliveryFulfilledEventIsReceived() {
        UUID deliveryId = UUID.randomUUID();

        kafkaDeliveriesMessageHandler.handle(
                new DeliveryFulfilledIntegrationEvent(OffsetDateTime.now(), deliveryId));

        verify(courierDeliveryService).fulfillDelivery(deliveryId);
    }

    @Test
    void shouldIgnoreUnmappedMessagesOnDefaultHandler() {
        kafkaDeliveriesMessageHandler.handle("any-unmapped-payload", "message-key", "10");

        verifyNoInteractions(courierDeliveryService);
    }

}
