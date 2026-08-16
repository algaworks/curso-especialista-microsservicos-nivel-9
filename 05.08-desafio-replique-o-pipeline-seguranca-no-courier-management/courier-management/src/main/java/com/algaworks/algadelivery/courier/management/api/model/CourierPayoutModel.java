package com.algaworks.algadelivery.courier.management.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourierPayoutModel {
    private BigDecimal payoutFee;
}
