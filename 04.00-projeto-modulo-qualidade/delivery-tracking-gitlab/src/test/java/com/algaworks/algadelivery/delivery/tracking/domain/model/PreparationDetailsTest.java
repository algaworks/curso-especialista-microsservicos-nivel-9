package com.algaworks.algadelivery.delivery.tracking.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class PreparationDetailsTest {

    @Test
    void shouldCreatePreparationDetailsWithBuilder() {
        ContactPoint sender = ContactPoint.builder()
                .zipCode("00000-000")
                .street("Rua São Paulo")
                .number("100")
                .name("João Silva")
                .phone("(11) 90000-1234")
                .build();

        ContactPoint recipient = ContactPoint.builder()
                .zipCode("12331-342")
                .street("Rua Brasil")
                .number("500")
                .name("Maria Silva")
                .phone("(11) 91345-1332")
                .build();

        Delivery.PreparationDetails details = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("15.00"))
                .courierPayout(new BigDecimal("5.00"))
                .expectedDeliveryTime(Duration.ofHours(5))
                .build();

        assertEquals(sender, details.getSender());
        assertEquals(recipient, details.getRecipient());
        assertEquals(new BigDecimal("15.00"), details.getDistanceFee());
        assertEquals(new BigDecimal("5.00"), details.getCourierPayout());
        assertEquals(Duration.ofHours(5), details.getExpectedDeliveryTime());
    }

    @Test
    void shouldCreatePreparationDetailsWithDifferentDurations() {
        ContactPoint sender = ContactPoint.builder()
                .zipCode("00000-000")
                .street("Rua São Paulo")
                .number("100")
                .name("João Silva")
                .phone("(11) 90000-1234")
                .build();

        ContactPoint recipient = ContactPoint.builder()
                .zipCode("12331-342")
                .street("Rua Brasil")
                .number("500")
                .name("Maria Silva")
                .phone("(11) 91345-1332")
                .build();

        Delivery.PreparationDetails details = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("20.00"))
                .courierPayout(new BigDecimal("8.00"))
                .expectedDeliveryTime(Duration.ofMinutes(45))
                .build();

        assertEquals(Duration.ofMinutes(45), details.getExpectedDeliveryTime());
    }
}
