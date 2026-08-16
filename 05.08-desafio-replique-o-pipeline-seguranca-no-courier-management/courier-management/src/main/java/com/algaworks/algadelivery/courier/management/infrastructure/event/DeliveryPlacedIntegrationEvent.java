package com.algaworks.algadelivery.courier.management.infrastructure.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPlacedIntegrationEvent {
    private OffsetDateTime occurredAt;
    private UUID deliveryId;
}