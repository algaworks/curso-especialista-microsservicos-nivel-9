package com.algaworks.algadelivery.courier.management.api.error;

import com.algaworks.algadelivery.courier.management.domain.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler apiExceptionHandler =
            new ApiExceptionHandler(new StaticMessageSource());

    @Test
    void shouldTranslateDomainExceptionToUnprocessableEntityProblemDetail() {
        ProblemDetail problemDetail = apiExceptionHandler
                .handleDomainBusinessException(new DomainException("Courier is not available"));

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Unprocessable Entity");
        assertThat(problemDetail.getDetail()).isEqualTo("Courier is not available");
        assertThat(problemDetail.getType()).isEqualTo(URI.create("/errors/unprocessable-entity"));
    }

}
