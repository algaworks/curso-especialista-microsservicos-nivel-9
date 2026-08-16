package com.algaworks.algadelivery.courier.management.domain.service;

import com.algaworks.algadelivery.courier.management.api.model.CourierInput;
import com.algaworks.algadelivery.courier.management.domain.exception.DomainEntityNotFoundException;
import com.algaworks.algadelivery.courier.management.domain.model.Courier;
import com.algaworks.algadelivery.courier.management.domain.model.CourierTestDataBuilder;
import com.algaworks.algadelivery.courier.management.domain.repository.CourierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourierRegistrationServiceTest {

    @Mock
    private CourierRepository courierRepository;

    @InjectMocks
    private CourierRegistrationService courierRegistrationService;

    @Test
    void shouldCreateBrandNewCourier() {
        CourierInput input = courierInput("John Doe", "11 99999-9999");
        when(courierRepository.saveAndFlush(any(Courier.class))).thenAnswer(answer -> answer.getArgument(0));

        Courier created = courierRegistrationService.create(input);

        ArgumentCaptor<Courier> captor = ArgumentCaptor.forClass(Courier.class);
        verify(courierRepository).saveAndFlush(captor.capture());

        assertThat(created).isSameAs(captor.getValue());
        assertThat(created.getName()).isEqualTo("John Doe");
        assertThat(created.getPhone()).isEqualTo("11 99999-9999");
        assertThat(created.getPendingDeliveriesQuantity()).isZero();
    }

    @Test
    void shouldUpdateExistingCourier() {
        Courier courier = CourierTestDataBuilder.brandNewCourier();
        when(courierRepository.findById(courier.getId())).thenReturn(Optional.of(courier));
        when(courierRepository.saveAndFlush(courier)).thenReturn(courier);

        Courier updated = courierRegistrationService.update(courier.getId(),
                courierInput("Jane Doe", "11 88888-8888"));

        assertThat(updated.getName()).isEqualTo("Jane Doe");
        assertThat(updated.getPhone()).isEqualTo("11 88888-8888");
        verify(courierRepository).saveAndFlush(courier);
    }

    @Test
    void shouldFailWhenUpdatingUnknownCourier() {
        UUID unknownCourierId = UUID.randomUUID();
        when(courierRepository.findById(unknownCourierId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(DomainEntityNotFoundException.class)
                .isThrownBy(() -> courierRegistrationService.update(unknownCourierId,
                        courierInput("Jane Doe", "11 88888-8888")));

        verify(courierRepository, never()).saveAndFlush(any(Courier.class));
    }

    private CourierInput courierInput(String name, String phone) {
        CourierInput input = new CourierInput();
        input.setName(name);
        input.setPhone(phone);
        return input;
    }

}
