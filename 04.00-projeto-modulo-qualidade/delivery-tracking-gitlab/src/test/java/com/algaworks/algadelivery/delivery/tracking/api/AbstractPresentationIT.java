package com.algaworks.algadelivery.delivery.tracking.api;

import com.algaworks.algadelivery.delivery.tracking.utils.TestcontainersConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static io.restassured.config.JsonConfig.jsonConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfig.class)
public abstract class AbstractPresentationIT {

    @LocalServerPort
    protected int port;

    protected static WireMockServer wireMockCourierManagement;

    protected void beforeEach() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.config().jsonConfig(jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    }

    protected static void initWireMock() {
        wireMockCourierManagement = new WireMockServer(options()
                .port(8782)
                .usingFilesUnderDirectory("src/test/resources/wiremock/courier-management"));

        wireMockCourierManagement.start();
    }

    protected static void stopMock() {
        wireMockCourierManagement.stop();
    }
}
