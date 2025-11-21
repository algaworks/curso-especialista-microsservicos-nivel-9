package com.algaworks.algadelivery.delivery.tracking.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void shouldCreateItemWithBrandNew() {
        Delivery delivery = Delivery.draft();
        
        Item item = Item.brandNew("Pizza Margherita", 2, delivery);

        assertNotNull(item.getId());
        assertEquals("Pizza Margherita", item.getName());
        assertEquals(2, item.getQuantity());
    }

    @Test
    void shouldBeEqualWhenSameId() {
        Delivery delivery = Delivery.draft();
        Item item1 = Item.brandNew("Pizza Margherita", 2, delivery);
        Item item2 = Item.brandNew("Refrigerante", 3, delivery);

        assertNotEquals(item1, item2);
    }

    @Test
    void shouldUpdateQuantity() {
        Delivery delivery = Delivery.draft();
        Item item = Item.brandNew("Pizza Margherita", 2, delivery);

        item.setQuantity(5);

        assertEquals(5, item.getQuantity());
    }

    @Test
    void shouldHaveDifferentHashCodeWhenDifferentId() {
        Delivery delivery = Delivery.draft();
        Item item1 = Item.brandNew("Pizza Margherita", 2, delivery);
        Item item2 = Item.brandNew("Pizza Margherita", 2, delivery);

        assertNotEquals(item1.hashCode(), item2.hashCode());
    }
}
