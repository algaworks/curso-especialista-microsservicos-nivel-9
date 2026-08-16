package com.algaworks.algadelivery.courier.management.domain.model;

import java.util.UUID;

public class CourierTestDataBuilder {

    private CourierTestDataBuilder() {
    }

    public static Courier brandNewCourier() {
        return Courier.brandNew("John Doe", "11 99999-9999");
    }

    public static Courier courierWithPendingDelivery(UUID deliveryId) {
        Courier courier = brandNewCourier();
        courier.assign(deliveryId);
        return courier;
    }

}
