package com.algaworks.algadelivery.delivery.tracking.domain.model;

import com.algaworks.algadelivery.delivery.tracking.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {

    @Test
    public void shouldCreateDraftDelivery() {
        Delivery delivery = Delivery.draft();

        assertNotNull(delivery.getId());
        assertEquals(DeliveryStatus.DRAFT, delivery.getStatus());
        assertEquals(0, delivery.getTotalItems());
        assertEquals(BigDecimal.ZERO, delivery.getTotalCost());
        assertEquals(BigDecimal.ZERO, delivery.getCourierPayout());
        assertEquals(BigDecimal.ZERO, delivery.getDistanceFee());
    }

    @Test
    public void shouldAddItem() {
        Delivery delivery = Delivery.draft();

        UUID itemId = delivery.addItem("Pizza Margherita", 2);

        assertNotNull(itemId);
        assertEquals(1, delivery.getItems().size());
        assertEquals(2, delivery.getTotalItems());
    }

    @Test
    public void shouldAddMultipleItems() {
        Delivery delivery = Delivery.draft();

        delivery.addItem("Pizza Margherita", 2);
        delivery.addItem("Refrigerante", 3);

        assertEquals(2, delivery.getItems().size());
        assertEquals(5, delivery.getTotalItems());
    }

    @Test
    public void shouldRemoveItem() {
        Delivery delivery = Delivery.draft();
        UUID itemId = delivery.addItem("Pizza Margherita", 2);

        delivery.removeItem(itemId);

        assertEquals(0, delivery.getItems().size());
        assertEquals(0, delivery.getTotalItems());
    }

    @Test
    public void shouldChangeItemQuantity() {
        Delivery delivery = Delivery.draft();
        UUID itemId = delivery.addItem("Pizza Margherita", 2);

        delivery.changeItemQuantity(itemId, 5);

        assertEquals(5, delivery.getTotalItems());
        assertEquals(5, delivery.getItems().get(0).getQuantity());
    }

    @Test
    public void shouldRemoveAllItems() {
        Delivery delivery = Delivery.draft();
        delivery.addItem("Pizza Margherita", 2);
        delivery.addItem("Refrigerante", 3);

        delivery.removeItems();

        assertEquals(0, delivery.getItems().size());
        assertEquals(0, delivery.getTotalItems());
    }

    @Test
    public void shouldEditPreparationDetails() {
        Delivery delivery = Delivery.draft();
        Delivery.PreparationDetails details = createdValidPreparationDetails();

        delivery.editPreparationDetails(details);

        assertEquals("João Silva", delivery.getSender().getName());
        assertEquals("Maria Silva", delivery.getRecipient().getName());
        assertEquals(new BigDecimal("15.00"), delivery.getDistanceFee());
        assertEquals(new BigDecimal("5.00"), delivery.getCourierPayout());
        assertEquals(new BigDecimal("20.00"), delivery.getTotalCost());
        assertNotNull(delivery.getExpectedDeliveryAt());
    }

    @Test
    public void shouldNotEditPreparationDetailsWhenNotDraft() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        delivery.place();

        assertThrows(DomainException.class, () -> {
            delivery.editPreparationDetails(createdValidPreparationDetails());
        });
    }

    @Test
    public void shouldChangeToPlaced() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());

        delivery.place();

        assertEquals(DeliveryStatus.WAITING_FOR_COURIER, delivery.getStatus());
        assertNotNull(delivery.getPlacedAt());
    }

    @Test
    public void shouldNotPlaceWithoutPreparationDetails() {
        Delivery delivery = Delivery.draft();
        
        assertThrows(DomainException.class, () -> {
            delivery.place();
        });

        assertEquals(DeliveryStatus.DRAFT, delivery.getStatus());
        assertNull(delivery.getPlacedAt());
    }

    @Test
    public void shouldNotPlaceWhenAlreadyPlaced() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        delivery.place();

        assertThrows(DomainException.class, () -> {
            delivery.place();
        });
    }

    @Test
    public void shouldPickUp() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        delivery.place();
        UUID courierId = UUID.randomUUID();

        delivery.pickUp(courierId);

        assertEquals(DeliveryStatus.IN_TRANSIT, delivery.getStatus());
        assertEquals(courierId, delivery.getCourierId());
        assertNotNull(delivery.getAssignedAt());
    }

    @Test
    public void shouldNotPickUpWithInvalidStatusTransition() {
        Delivery delivery = Delivery.draft();
        UUID courierId = UUID.randomUUID();

        assertThrows(DomainException.class, () -> {
            delivery.pickUp(courierId);
        });

        assertEquals(DeliveryStatus.DRAFT, delivery.getStatus());
    }

    @Test
    public void shouldMarkAsDelivered() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        delivery.place();
        delivery.pickUp(UUID.randomUUID());

        delivery.markAsDelivered();

        assertEquals(DeliveryStatus.DELIVERED, delivery.getStatus());
        assertNotNull(delivery.getFulfilledAt());
    }

    @Test
    public void shouldNotMarkAsDeliveredWithInvalidStatusTransition() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        delivery.place();

        assertThrows(DomainException.class, () -> {
            delivery.markAsDelivered();
        });

        assertEquals(DeliveryStatus.WAITING_FOR_COURIER, delivery.getStatus());
    }

    @Test
    public void shouldReturnUnmodifiableItemsList() {
        Delivery delivery = Delivery.draft();
        delivery.addItem("Pizza Margherita", 2);

        assertThrows(UnsupportedOperationException.class, () -> {
            delivery.getItems().clear();
        });
    }

    private Delivery.PreparationDetails createdValidPreparationDetails() {
        ContactPoint sender = ContactPoint.builder()
                .zipCode("00000-000")
                .street("Rua São Paulo")
                .number("100")
                .complement("Sala 401")
                .name("João Silva")
                .phone("(11) 90000-1234")
                .build();

        ContactPoint recipient = ContactPoint.builder()
                .zipCode("12331-342")
                .street("Rua Brasil")
                .number("500")
                .complement("")
                .name("Maria Silva")
                .phone("(11) 91345-1332")
                .build();

        return Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(new BigDecimal("15.00"))
                .courierPayout(new BigDecimal("5.00"))
                .expectedDeliveryTime(Duration.ofHours(5))
                .build();
    }
}