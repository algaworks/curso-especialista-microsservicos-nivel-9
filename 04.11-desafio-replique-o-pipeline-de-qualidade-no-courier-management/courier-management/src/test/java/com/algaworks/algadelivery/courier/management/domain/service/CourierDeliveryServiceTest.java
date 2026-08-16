package com.algaworks.algadelivery.courier.management.domain.service;

import com.algaworks.algadelivery.courier.management.domain.exception.DomainException;
import com.algaworks.algadelivery.courier.management.domain.model.Courier;
import com.algaworks.algadelivery.courier.management.domain.model.CourierTestDataBuilder;
import com.algaworks.algadelivery.courier.management.domain.repository.CourierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourierDeliveryServiceTest {

    @Mock
    private CourierRepository courierRepository;

    @InjectMocks
    private CourierDeliveryService courierDeliveryService;

    @Test
    void shouldAssignDeliveryToTheIdlestCourier() {
        UUID deliveryId = UUID.randomUUID();
        Courier courier = CourierTestDataBuilder.brandNewCourier();

        when(courierRepository.existsByPendingDeliveries_id(deliveryId)).thenReturn(false);
        when(courierRepository.findTop1ByOrderByLastFulfilledDeliveryAtAsc()).thenReturn(Optional.of(courier));

        courierDeliveryService.assignDelivery(deliveryId);

        assertThat(courier.getPendingDeliveriesQuantity()).isEqualTo(1);
        assertThat(courier.getPendingDeliveries())
                .anyMatch(assignedDelivery -> assignedDelivery.getId().equals(deliveryId));
    }

    @Test
    void shouldFailWhenDeliveryIsAlreadyAssigned() {
        UUID deliveryId = UUID.randomUUID();
        when(courierRepository.existsByPendingDeliveries_id(deliveryId)).thenReturn(true);

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> courierDeliveryService.assignDelivery(deliveryId));
    }

    @Test
    void shouldFailWhenThereIsNoCourierAvailable() {
        UUID deliveryId = UUID.randomUUID();
        when(courierRepository.existsByPendingDeliveries_id(deliveryId)).thenReturn(false);
        when(courierRepository.findTop1ByOrderByLastFulfilledDeliveryAtAsc()).thenReturn(Optional.empty());

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> courierDeliveryService.assignDelivery(deliveryId));
    }

    @Test
    void shouldFulfillDeliveryAssignedToCourier() {
        UUID deliveryId = UUID.randomUUID();
        Courier courier = CourierTestDataBuilder.courierWithPendingDelivery(deliveryId);

        when(courierRepository.findByPendingDeliveries_id(deliveryId)).thenReturn(Optional.of(courier));

        courierDeliveryService.fulfillDelivery(deliveryId);

        assertThat(courier.getFulfilledDeliveriesQuantity()).isEqualTo(1);
        assertThat(courier.getPendingDeliveriesQuantity()).isZero();
        assertThat(courier.getPendingDeliveries()).isEmpty();
    }

    @Test
    void shouldFailWhenFulfillingDeliveryWithoutCourier() {
        UUID deliveryId = UUID.randomUUID();
        when(courierRepository.findByPendingDeliveries_id(deliveryId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(DomainException.class)
                .isThrownBy(() -> courierDeliveryService.fulfillDelivery(deliveryId));
    }

}
