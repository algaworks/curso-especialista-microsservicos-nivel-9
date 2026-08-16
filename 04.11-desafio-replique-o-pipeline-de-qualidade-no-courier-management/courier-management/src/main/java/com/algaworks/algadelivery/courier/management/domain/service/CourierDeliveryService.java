package com.algaworks.algadelivery.courier.management.domain.service;

import com.algaworks.algadelivery.courier.management.domain.exception.DomainException;
import com.algaworks.algadelivery.courier.management.domain.model.Courier;
import com.algaworks.algadelivery.courier.management.domain.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourierDeliveryService {

    private final CourierRepository courierRepository;

    @Transactional
    public void assignDelivery(UUID deliveryId) {
        if (courierRepository.existsByPendingDeliveries_id(deliveryId)) {
            throw new DomainException();
        }

        Courier courier = courierRepository.findTop1ByOrderByLastFulfilledDeliveryAtAsc()
                .orElseThrow(()-> new DomainException());

        courier.assign(deliveryId);

        log.info("Courier {} assigned to delivery {}", courier.getId(), deliveryId);
    }


    @Transactional
    public void fulfillDelivery(UUID deliveryId) {
        Courier courier = courierRepository.findByPendingDeliveries_id(deliveryId)
                .orElseThrow(()-> new DomainException());

        courier.fulfill(deliveryId);

        log.info("Courier {} fulfilled the delivery {}", courier.getId(), deliveryId);
    }

}
