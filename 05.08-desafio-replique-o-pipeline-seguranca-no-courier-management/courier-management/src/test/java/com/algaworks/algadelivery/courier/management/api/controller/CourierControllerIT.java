package com.algaworks.algadelivery.courier.management.api.controller;

import com.algaworks.algadelivery.courier.management.api.AbstractPresentationIT;
import com.algaworks.algadelivery.courier.management.api.model.CourierInput;
import com.algaworks.algadelivery.courier.management.api.model.CourierPayoutCalculateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class CourierControllerIT extends AbstractPresentationIT {

    private static final String COURIERS_PATH = "/api/v1/couriers";

    @BeforeEach
    void setUp() {
        beforeEach();
    }

    @Test
    void shouldCreateCourier() {
        given()
            .contentType(JSON)
            .body(courierInput("John Doe", "11 99999-9999"))
        .when()
            .post(COURIERS_PATH)
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("name", equalTo("John Doe"))
            .body("phone", equalTo("11 99999-9999"))
            .body("pendingDeliveriesQuantity", equalTo(0))
            .body("fulfilledDeliveriesQuantity", equalTo(0));
    }

    @Test
    void shouldRejectCourierWithBlankFields() {
        given()
            .contentType(JSON)
            .body(courierInput("", ""))
        .when()
            .post(COURIERS_PATH)
        .then()
            .statusCode(400)
            .body("title", equalTo("Invalid fields"))
            .body("fields.name", notNullValue())
            .body("fields.phone", notNullValue());
    }

    @Test
    void shouldFindCourierById() {
        String courierId = createCourier("Jane Doe", "11 88888-8888");

        given()
            .accept(JSON)
        .when()
            .get(COURIERS_PATH + "/{courierId}", courierId)
        .then()
            .statusCode(200)
            .body("id", equalTo(courierId))
            .body("name", equalTo("Jane Doe"));
    }

    @Test
    void shouldReturnNotFoundForUnknownCourier() {
        given()
            .accept(JSON)
        .when()
            .get(COURIERS_PATH + "/{courierId}", UUID.randomUUID())
        .then()
            .statusCode(404);
    }

    @Test
    void shouldListCouriers() {
        createCourier("Listed Courier", "11 77777-7777");

        given()
            .accept(JSON)
        .when()
            .get(COURIERS_PATH)
        .then()
            .statusCode(200)
            .body("content", notNullValue());
    }

    @Test
    void shouldUpdateCourier() {
        String courierId = createCourier("Old Name", "11 66666-6666");

        given()
            .contentType(JSON)
            .body(courierInput("New Name", "11 55555-5555"))
        .when()
            .put(COURIERS_PATH + "/{courierId}", courierId)
        .then()
            .statusCode(204);

        given()
            .accept(JSON)
        .when()
            .get(COURIERS_PATH + "/{courierId}", courierId)
        .then()
            .statusCode(200)
            .body("name", equalTo("New Name"))
            .body("phone", equalTo("11 55555-5555"));
    }

    @Test
    void shouldCalculateCourierPayout() {
        given()
            .contentType(JSON)
            .body(new CourierPayoutCalculateRequest(10.0))
        .when()
            .post(COURIERS_PATH + "/payout-calculation")
        .then()
            .statusCode(200)
            .body("payoutFee", comparesEqualTo(new BigDecimal("30.00")));
    }

    private String createCourier(String name, String phone) {
        return given()
                .contentType(JSON)
                .body(courierInput(name, phone))
            .when()
                .post(COURIERS_PATH)
            .then()
                .statusCode(201)
                .extract().path("id");
    }

    private CourierInput courierInput(String name, String phone) {
        CourierInput input = new CourierInput();
        input.setName(name);
        input.setPhone(phone);
        return input;
    }

}
