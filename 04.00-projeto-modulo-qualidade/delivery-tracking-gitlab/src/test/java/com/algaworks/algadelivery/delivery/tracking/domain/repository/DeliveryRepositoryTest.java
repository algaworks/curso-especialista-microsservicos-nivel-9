package com.algaworks.algadelivery.delivery.tracking.domain.repository;

import com.algaworks.algadelivery.delivery.tracking.AbstractTestContainers;
import com.algaworks.algadelivery.delivery.tracking.domain.model.ContactPoint;
import com.algaworks.algadelivery.delivery.tracking.domain.model.Delivery;
import com.algaworks.algadelivery.delivery.tracking.domain.model.DeliveryStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DeliveryRepositoryTest extends AbstractTestContainers {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    void shouldSaveDeliveryWithItems() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        delivery.addItem("Computador", 2);
        delivery.addItem("Notebook", 3);

        Delivery savedDelivery = deliveryRepository.saveAndFlush(delivery);

        assertNotNull(savedDelivery.getId());
        assertEquals(2, savedDelivery.getItems().size());
        assertEquals(5, savedDelivery.getTotalItems());
    }

    @Test
    void shouldFindDeliveryById() {
        Delivery delivery = createAndSaveDelivery();

        Optional<Delivery> foundDelivery = deliveryRepository.findById(delivery.getId());

        assertTrue(foundDelivery.isPresent());
        assertEquals(delivery.getId(), foundDelivery.get().getId());
        assertEquals("João Silva", foundDelivery.get().getSender().getName());
        assertEquals("Maria Silva", foundDelivery.get().getRecipient().getName());
    }

    @Test
    void shouldReturnEmptyWhenDeliveryNotFound() {
        UUID randomId = UUID.randomUUID();

        Optional<Delivery> foundDelivery = deliveryRepository.findById(randomId);

        assertFalse(foundDelivery.isPresent());
    }

    @Test
    void shouldUpdateDeliveryStatus() {
        Delivery delivery = createAndSaveDelivery();
        delivery.place();

        deliveryRepository.saveAndFlush(delivery);
        Delivery updatedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();

        assertEquals(DeliveryStatus.WAITING_FOR_COURIER, updatedDelivery.getStatus());
        assertNotNull(updatedDelivery.getPlacedAt());
    }

    @Test
    void shouldDeleteDelivery() {
        Delivery delivery = createAndSaveDelivery();
        UUID deliveryId = delivery.getId();

        deliveryRepository.delete(delivery);
        deliveryRepository.flush();

        Optional<Delivery> deletedDelivery = deliveryRepository.findById(deliveryId);
        assertFalse(deletedDelivery.isPresent());
    }

    @Test
    void shouldFindAllDeliveries() {
        Delivery delivery1 = createAndSaveDelivery();
        Delivery delivery2 = createAndSaveDelivery();

        List<Delivery> deliveries = deliveryRepository.findAll();

        assertTrue(deliveries.size() >= 2);
        assertTrue(deliveries.stream().anyMatch(d -> d.getId().equals(delivery1.getId())));
        assertTrue(deliveries.stream().anyMatch(d -> d.getId().equals(delivery2.getId())));
    }

    @Test
    void shouldCountDeliveries() {
        deliveryRepository.deleteAll();
        createAndSaveDelivery();
        createAndSaveDelivery();
        createAndSaveDelivery();

        long count = deliveryRepository.count();

        assertEquals(3, count);
    }

    @Test
    void shouldCheckIfDeliveryExists() {
        Delivery delivery = createAndSaveDelivery();

        boolean exists = deliveryRepository.existsById(delivery.getId());

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenDeliveryDoesNotExist() {
        UUID randomId = UUID.randomUUID();

        boolean exists = deliveryRepository.existsById(randomId);

        assertFalse(exists);
    }

    @Test
    void shouldPersistDeliveryWithCourierAssigned() {
        Delivery delivery = createAndSaveDelivery();
        delivery.place();
        UUID courierId = UUID.randomUUID();
        delivery.pickUp(courierId);

        deliveryRepository.saveAndFlush(delivery);
        Delivery persistedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();

        assertEquals(DeliveryStatus.IN_TRANSIT, persistedDelivery.getStatus());
        assertEquals(courierId, persistedDelivery.getCourierId());
        assertNotNull(persistedDelivery.getAssignedAt());
    }

    @Test
    void shouldPersistCompleteDeliveryLifecycle() {
        Delivery delivery = createAndSaveDelivery();
        
        // Place
        delivery.place();
        deliveryRepository.saveAndFlush(delivery);
        
        // Pick up
        UUID courierId = UUID.randomUUID();
        delivery.pickUp(courierId);
        deliveryRepository.saveAndFlush(delivery);
        
        // Mark as delivered
        delivery.markAsDelivered();
        deliveryRepository.saveAndFlush(delivery);

        Delivery finalDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();

        assertEquals(DeliveryStatus.DELIVERED, finalDelivery.getStatus());
        assertNotNull(finalDelivery.getPlacedAt());
        assertNotNull(finalDelivery.getAssignedAt());
        assertNotNull(finalDelivery.getFulfilledAt());
        assertEquals(courierId, finalDelivery.getCourierId());
    }

    @Test
    void shouldPersistDeliveryWithMultipleItems() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        
        delivery.addItem("Pizza Margherita", 2);
        delivery.addItem("Pizza Calabresa", 1);
        delivery.addItem("Refrigerante 2L", 3);
        delivery.addItem("Água", 2);

        deliveryRepository.saveAndFlush(delivery);
        Delivery persistedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();

        assertEquals(4, persistedDelivery.getItems().size());
        assertEquals(8, persistedDelivery.getTotalItems());
    }

    @Test
    void shouldPersistDeliveryWithRemovedItems() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        
        UUID itemId1 = delivery.addItem("Pizza Margherita", 2);
        UUID itemId2 = delivery.addItem("Pizza Calabresa", 1);
        delivery.addItem("Refrigerante 2L", 3);

        delivery.removeItem(itemId1);
        delivery.removeItem(itemId2);

        deliveryRepository.saveAndFlush(delivery);
        Delivery persistedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();

        assertEquals(1, persistedDelivery.getItems().size());
        assertEquals(3, persistedDelivery.getTotalItems());
    }

    @Test
    void shouldPersistDeliveryWithUpdatedItemQuantity() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        
        UUID itemId = delivery.addItem("Pizza Margherita", 2);
        delivery.changeItemQuantity(itemId, 5);

        deliveryRepository.saveAndFlush(delivery);
        Delivery persistedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();

        assertEquals(1, persistedDelivery.getItems().size());
        assertEquals(5, persistedDelivery.getTotalItems());
        assertEquals(5, persistedDelivery.getItems().get(0).getQuantity());
    }

    @Test
    void shouldPersistDeliveryWithTotalCostCalculation() {
        Delivery delivery = Delivery.draft();
        
        ContactPoint sender = createSender();
        ContactPoint recipient = createRecipient();
        
        BigDecimal distanceFee = new BigDecimal("25.50");
        BigDecimal courierPayout = new BigDecimal("10.75");
        
        Delivery.PreparationDetails details = Delivery.PreparationDetails.builder()
                .sender(sender)
                .recipient(recipient)
                .distanceFee(distanceFee)
                .courierPayout(courierPayout)
                .expectedDeliveryTime(Duration.ofHours(3))
                .build();
        
        delivery.editPreparationDetails(details);

        deliveryRepository.saveAndFlush(delivery);
        Delivery persistedDelivery = deliveryRepository.findById(delivery.getId()).orElseThrow();

        assertEquals(new BigDecimal("36.25"), persistedDelivery.getTotalCost());
        assertEquals(distanceFee, persistedDelivery.getDistanceFee());
        assertEquals(courierPayout, persistedDelivery.getCourierPayout());
    }

    private Delivery createAndSaveDelivery() {
        Delivery delivery = Delivery.draft();
        delivery.editPreparationDetails(createdValidPreparationDetails());
        delivery.addItem("Computador", 2);
        delivery.addItem("Mouse", 1);
        return deliveryRepository.saveAndFlush(delivery);
    }

    private Delivery.PreparationDetails createdValidPreparationDetails() {
        return Delivery.PreparationDetails.builder()
                .sender(createSender())
                .recipient(createRecipient())
                .distanceFee(new BigDecimal("15.00"))
                .courierPayout(new BigDecimal("5.00"))
                .expectedDeliveryTime(Duration.ofHours(5))
                .build();
    }

    private ContactPoint createSender() {
        return ContactPoint.builder()
                .zipCode("00000-000")
                .street("Rua São Paulo")
                .number("100")
                .complement("Sala 401")
                .name("João Silva")
                .phone("(11) 90000-1234")
                .build();
    }

    private ContactPoint createRecipient() {
        return ContactPoint.builder()
                .zipCode("12331-342")
                .street("Rua Brasil")
                .number("500")
                .complement("")
                .name("Maria Silva")
                .phone("(11) 91345-1332")
                .build();
    }
}