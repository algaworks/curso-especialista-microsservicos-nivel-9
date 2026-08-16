package com.algaworks.algadelivery.courier.management.domain.service;

import com.algaworks.algadelivery.courier.management.api.model.CourierInput;
import com.algaworks.algadelivery.courier.management.domain.exception.DomainEntityNotFoundException;
import com.algaworks.algadelivery.courier.management.domain.model.Courier;
import com.algaworks.algadelivery.courier.management.domain.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourierRegistrationService {

    private final CourierRepository courierRepository;

    @Transactional
    public Courier create(CourierInput courierInput) {
        Courier courier = Courier.brandNew(courierInput.getName(), courierInput.getPhone());
        return courierRepository.saveAndFlush(courier);
    }

    @Transactional
    public Courier update(UUID courierId, CourierInput input) {
        Courier courier = courierRepository.findById(courierId)
                .orElseThrow(() -> new DomainEntityNotFoundException());

        courier.setName(input.getName());
        courier.setPhone(input.getPhone());

        return courierRepository.saveAndFlush(courier);
    }

}
