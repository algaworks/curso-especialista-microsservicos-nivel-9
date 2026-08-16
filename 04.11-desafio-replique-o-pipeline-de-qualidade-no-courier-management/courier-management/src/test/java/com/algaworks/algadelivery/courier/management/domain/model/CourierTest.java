package com.algaworks.algadelivery.courier.management.domain.model;

import com.algaworks.algadelivery.courier.management.domain.event.CourierAssigned;
import com.algaworks.algadelivery.courier.management.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowable;

class CourierTest {

    @Test
    void shouldCreateBrandNewCourierWithZeroedCounters() {
        Courier courier = Courier.brandNew("John Doe", "11 99999-9999");

        assertThat(courier.getId()).isNotNull();
        assertThat(courier.getName()).isEqualTo("John Doe");
        assertThat(courier.getPhone()).isEqualTo("11 99999-9999");
        assertThat(courier.getFulfilledDeliveriesQuantity()).isZero();
        assertThat(courier.getPendingDeliveriesQuantity()).isZero();
        assertThat(courier.getLastFulfilledDeliveryAt()).isNull();
        assertThat(courier.getPendingDeliveries()).isEmpty();
    }

    @Test
    void shouldAssignDeliveryAndRegisterDomainEvent() {
        UUID deliveryId = UUID.randomUUID();
        Courier courier = CourierTestDataBuilder.brandNewCourier();

        courier.assign(deliveryId);

        assertThat(courier.getPendingDeliveriesQuantity()).isEqualTo(1);
        assertThat(courier.getPendingDeliveries())
                .extracting(AssignedDelivery::getId)
                .containsExactly(deliveryId);

        assertThat(domainEventsOf(courier))
                .hasSize(1)
                .first()
                .isInstanceOfSatisfying(CourierAssigned.class, event -> {
                    assertThat(event.getCourierId()).isEqualTo(courier.getId());
                    assertThat(event.getDeliveryId()).isEqualTo(deliveryId);
                    assertThat(event.getOccurredAt()).isNotNull();
                });
    }

    @Test
    void shouldFulfillPendingDelivery() {
        UUID deliveryId = UUID.randomUUID();
        Courier courier = CourierTestDataBuilder.courierWithPendingDelivery(deliveryId);

        courier.fulfill(deliveryId);

        assertThat(courier.getPendingDeliveries()).isEmpty();
        assertThat(courier.getPendingDeliveriesQuantity()).isZero();
        assertThat(courier.getFulfilledDeliveriesQuantity()).isEqualTo(1);
        assertThat(courier.getLastFulfilledDeliveryAt()).isNotNull();
    }

    @Test
    void shouldFailWhenFulfillingUnknownDelivery() {
        Courier courier = CourierTestDataBuilder.brandNewCourier();
        UUID unknownDeliveryId = UUID.randomUUID();

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> courier.fulfill(unknownDeliveryId));
    }

    @Test
    void shouldNotExposeMutablePendingDeliveries() {
        Courier courier = CourierTestDataBuilder.courierWithPendingDelivery(UUID.randomUUID());

        Iterable<AssignedDelivery> pendingDeliveries = courier.getPendingDeliveries();

        Throwable thrown = catchThrowable(() ->
                ((Collection<AssignedDelivery>) pendingDeliveries).clear());

        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @SuppressWarnings("unchecked")
    private Collection<Object> domainEventsOf(Courier courier) {
        return (Collection<Object>) ReflectionTestUtils.getField(courier, "domainEvents");
    }

}
