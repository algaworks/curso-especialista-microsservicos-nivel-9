package com.algaworks.algadelivery.courier.management.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(of = "id")
public class AssignedDelivery {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Courier courier;

    private OffsetDateTime assignedAt;

    public static AssignedDelivery pending(UUID deliveryId, Courier courier) {
        return new AssignedDelivery(
                deliveryId,
                courier,
                OffsetDateTime.now()
        );
    }

}