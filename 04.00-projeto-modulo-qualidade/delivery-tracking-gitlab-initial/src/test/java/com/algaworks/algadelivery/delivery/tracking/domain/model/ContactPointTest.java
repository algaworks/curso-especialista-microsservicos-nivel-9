package com.algaworks.algadelivery.delivery.tracking.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactPointTest {

    @Test
    void shouldCreateContactPointWithBuilder() {
        ContactPoint contactPoint = ContactPoint.builder()
                .zipCode("12345-678")
                .street("Rua Exemplo")
                .number("123")
                .complement("Apto 45")
                .name("João Silva")
                .phone("(11) 99999-8888")
                .build();

        assertEquals("12345-678", contactPoint.getZipCode());
        assertEquals("Rua Exemplo", contactPoint.getStreet());
        assertEquals("123", contactPoint.getNumber());
        assertEquals("Apto 45", contactPoint.getComplement());
        assertEquals("João Silva", contactPoint.getName());
        assertEquals("(11) 99999-8888", contactPoint.getPhone());
    }

    @Test
    void shouldBeEqualWhenSameData() {
        ContactPoint contact1 = ContactPoint.builder()
                .zipCode("12345-678")
                .street("Rua Exemplo")
                .number("123")
                .complement("Apto 45")
                .name("João Silva")
                .phone("(11) 99999-8888")
                .build();

        ContactPoint contact2 = ContactPoint.builder()
                .zipCode("12345-678")
                .street("Rua Exemplo")
                .number("123")
                .complement("Apto 45")
                .name("João Silva")
                .phone("(11) 99999-8888")
                .build();

        assertEquals(contact1, contact2);
        assertEquals(contact1.hashCode(), contact2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenDifferentData() {
        ContactPoint contact1 = ContactPoint.builder()
                .zipCode("12345-678")
                .street("Rua Exemplo")
                .number("123")
                .complement("Apto 45")
                .name("João Silva")
                .phone("(11) 99999-8888")
                .build();

        ContactPoint contact2 = ContactPoint.builder()
                .zipCode("98765-432")
                .street("Rua Diferente")
                .number("456")
                .complement("Casa")
                .name("Maria Santos")
                .phone("(11) 88888-7777")
                .build();

        assertNotEquals(contact1, contact2);
    }

    @Test
    void shouldCreateContactPointWithEmptyComplement() {
        ContactPoint contactPoint = ContactPoint.builder()
                .zipCode("12345-678")
                .street("Rua Exemplo")
                .number("123")
                .complement("")
                .name("João Silva")
                .phone("(11) 99999-8888")
                .build();

        assertEquals("", contactPoint.getComplement());
    }
}
