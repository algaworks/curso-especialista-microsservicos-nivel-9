package com.algaworks.algadelivery.courier.management.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CourierCalculationServiceTest {

    private final CourierCalculationService courierCalculationService = new CourierCalculationService();

    @ParameterizedTest
    @CsvSource({
            "0.0, 10.00",
            "1.0, 12.00",
            "10.0, 30.00",
            "2.5, 15.00"
    })
    void shouldCalculatePayoutBasedOnDistance(Double distanceInKm, String expectedPayout) {
        BigDecimal payout = courierCalculationService.calculate(distanceInKm);

        assertThat(payout).isEqualByComparingTo(new BigDecimal(expectedPayout));
    }

    @Test
    void shouldAlwaysReturnPayoutWithTwoDecimalPlaces() {
        BigDecimal payout = courierCalculationService.calculate(3.333);

        assertThat(payout.scale()).isEqualTo(2);
        assertThat(payout).isEqualByComparingTo(new BigDecimal("16.67"));
    }

}
