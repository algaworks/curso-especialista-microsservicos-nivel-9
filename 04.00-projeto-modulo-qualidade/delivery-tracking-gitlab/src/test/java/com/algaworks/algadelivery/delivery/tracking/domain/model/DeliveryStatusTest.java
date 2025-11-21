package com.algaworks.algadelivery.delivery.tracking.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatusTest {

    @Test
    void draft_canChangeToWaitingForCourier() {
        assertTrue(DeliveryStatus.DRAFT.canChangeTo(DeliveryStatus.WAITING_FOR_COURIER));
    }

    @Test
    void draft_canNotChangeToInTransit() {
        assertTrue(DeliveryStatus.DRAFT.canNotChangeTo(DeliveryStatus.IN_TRANSIT));
    }

    @Test
    void draft_canNotChangeToDelivered() {
        assertTrue(DeliveryStatus.DRAFT.canNotChangeTo(DeliveryStatus.DELIVERED));
    }

    @Test
    void waitingForCourier_canChangeToInTransit() {
        assertTrue(DeliveryStatus.WAITING_FOR_COURIER.canChangeTo(DeliveryStatus.IN_TRANSIT));
    }

    @Test
    void waitingForCourier_canNotChangeToDraft() {
        assertTrue(DeliveryStatus.WAITING_FOR_COURIER.canNotChangeTo(DeliveryStatus.DRAFT));
    }

    @Test
    void waitingForCourier_canNotChangeToDelivered() {
        assertTrue(DeliveryStatus.WAITING_FOR_COURIER.canNotChangeTo(DeliveryStatus.DELIVERED));
    }

    @Test
    void inTransit_canChangeToDelivered() {
        assertTrue(DeliveryStatus.IN_TRANSIT.canChangeTo(DeliveryStatus.DELIVERED));
    }

    @Test
    void inTransit_canNotChangeToDraft() {
        assertTrue(DeliveryStatus.IN_TRANSIT.canNotChangeTo(DeliveryStatus.DRAFT));
    }

    @Test
    void inTransit_canNotChangeToWaitingForCourier() {
        assertTrue(DeliveryStatus.IN_TRANSIT.canNotChangeTo(DeliveryStatus.WAITING_FOR_COURIER));
    }

    @Test
    void delivered_canNotChangeToAnyStatus() {
        assertTrue(DeliveryStatus.DELIVERED.canNotChangeTo(DeliveryStatus.DRAFT));
        assertTrue(DeliveryStatus.DELIVERED.canNotChangeTo(DeliveryStatus.WAITING_FOR_COURIER));
        assertTrue(DeliveryStatus.DELIVERED.canNotChangeTo(DeliveryStatus.IN_TRANSIT));
    }
}