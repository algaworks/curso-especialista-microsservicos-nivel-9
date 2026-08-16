package com.algaworks.algadelivery.courier.management.domain.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CourierCalculationService {
    public BigDecimal calculate(Double distanceInKm) {
        BigDecimal amountPerKm = BigDecimal.TWO;
        BigDecimal basePayout = BigDecimal.TEN;
        return basePayout.add(amountPerKm.multiply(new BigDecimal(distanceInKm))).setScale(2, RoundingMode.HALF_EVEN);
    }
}
