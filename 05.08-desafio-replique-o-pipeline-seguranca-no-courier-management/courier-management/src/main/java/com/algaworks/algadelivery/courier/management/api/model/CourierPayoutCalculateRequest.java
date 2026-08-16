package com.algaworks.algadelivery.courier.management.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourierPayoutCalculateRequest {
    private Double distanceInKm;
}
