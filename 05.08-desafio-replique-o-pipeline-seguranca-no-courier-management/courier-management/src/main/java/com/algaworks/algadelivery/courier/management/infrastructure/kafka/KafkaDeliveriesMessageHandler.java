package com.algaworks.algadelivery.courier.management.infrastructure.kafka;

import com.algaworks.algadelivery.courier.management.domain.service.CourierDeliveryService;
import com.algaworks.algadelivery.courier.management.infrastructure.event.DeliveryFulfilledIntegrationEvent;
import com.algaworks.algadelivery.courier.management.infrastructure.event.DeliveryPlacedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "deliveries.v1.events"
},
        groupId = "courier-management"
)
@Slf4j
@RequiredArgsConstructor
public class KafkaDeliveriesMessageHandler {

    private final CourierDeliveryService courierDeliveryService;

    @KafkaHandler(isDefault = true)
    public void handle(@Payload Object message,
                       @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
                       @Header(value = KafkaHeaders.OFFSET, required = false) String messageOffset) {
        log.info("""
                \nDefault Handler:
                key={}
                offset={}
                payload={}
                """, messageKey, messageOffset, message);
    }

    @KafkaHandler
    public void handle(@Payload DeliveryPlacedIntegrationEvent event
    ) {
        log.info("Received: {}", event);
        courierDeliveryService.assignDelivery(event.getDeliveryId());
    }

    @KafkaHandler
    public void handle(@Payload DeliveryFulfilledIntegrationEvent event
    ) {
        log.info("Received: {}", event);
        courierDeliveryService.fulfillDelivery(event.getDeliveryId());
    }

}
