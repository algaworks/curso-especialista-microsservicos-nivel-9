package com.algaworks.algadelivery.courier.management.domain.repository;

import com.algaworks.algadelivery.courier.management.domain.model.Courier;
import com.algaworks.algadelivery.courier.management.domain.model.CourierTestDataBuilder;
import com.algaworks.algadelivery.courier.management.infrastructure.persistence.AbstractPersistenceIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CourierRepositoryIT extends AbstractPersistenceIT {

    @Autowired
    private CourierRepository courierRepository;

    @Test
    void shouldPersistCourierWithPendingDeliveries() {
        UUID deliveryId = UUID.randomUUID();
        Courier courier = CourierTestDataBuilder.courierWithPendingDelivery(deliveryId);

        courierRepository.saveAndFlush(courier);

        Optional<Courier> found = courierRepository.findById(courier.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getPendingDeliveriesQuantity()).isEqualTo(1);
        assertThat(found.get().getPendingDeliveries())
                .extracting("id")
                .containsExactly(deliveryId);
    }

    @Test
    void shouldFindCourierByPendingDeliveryId() {
        UUID deliveryId = UUID.randomUUID();
        Courier courier = CourierTestDataBuilder.courierWithPendingDelivery(deliveryId);
        courierRepository.saveAndFlush(courier);

        assertThat(courierRepository.existsByPendingDeliveries_id(deliveryId)).isTrue();
        assertThat(courierRepository.findByPendingDeliveries_id(deliveryId))
                .map(Courier::getId)
                .contains(courier.getId());
    }

    @Test
    void shouldNotFindCourierByUnknownPendingDeliveryId() {
        UUID unknownDeliveryId = UUID.randomUUID();

        assertThat(courierRepository.existsByPendingDeliveries_id(unknownDeliveryId)).isFalse();
        assertThat(courierRepository.findByPendingDeliveries_id(unknownDeliveryId)).isEmpty();
    }

    @Test
    void shouldFindTheIdlestCourierFirst() throws InterruptedException {
        Courier idleCourier = fulfilledCourier();
        Thread.sleep(10);
        Courier busyCourier = fulfilledCourier();

        courierRepository.saveAndFlush(busyCourier);
        courierRepository.saveAndFlush(idleCourier);

        Optional<Courier> idlest = courierRepository.findTop1ByOrderByLastFulfilledDeliveryAtAsc();

        assertThat(idlest).map(Courier::getId).contains(idleCourier.getId());
    }

    private Courier fulfilledCourier() {
        UUID deliveryId = UUID.randomUUID();
        Courier courier = CourierTestDataBuilder.courierWithPendingDelivery(deliveryId);
        courier.fulfill(deliveryId);
        return courier;
    }

}
