package com.algaworks.algadelivery.courier.management.domain.event;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CourierAssigned {
    private final OffsetDateTime occurredAt;
    private final UUID courierId;
    private final UUID deliveryId;
}
