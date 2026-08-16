package com.algaworks.algadelivery.courier.management.domain.model;

import com.algaworks.algadelivery.courier.management.domain.event.CourierAssigned;
import com.algaworks.algadelivery.courier.management.domain.exception.DomainException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(of = "id")
public class Courier extends AbstractAggregateRoot<Courier> {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Setter
    private String name;

    @Setter
    private String phone;

    private Integer fulfilledDeliveriesQuantity;

    private Integer pendingDeliveriesQuantity;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "courier")
    private List<AssignedDelivery> pendingDeliveries = new ArrayList<>();

    private OffsetDateTime lastFulfilledDeliveryAt;

    public static Courier brandNew(String name, String phone) {
        return new Courier(
                UUID.randomUUID(),
                name,
                phone,
                0,
                0,
                new ArrayList<>(),
                null
        );
    }

    public void assign(UUID deliveryId) {
        this.pendingDeliveries.add(
                AssignedDelivery.pending(deliveryId, this)
        );

        this.pendingDeliveriesQuantity++;

        this.registerEvent(
                new CourierAssigned(
                        OffsetDateTime.now(),
                        this.id,
                        deliveryId
                )
        );
    }

    public void fulfill(UUID deliveryId) {
        AssignedDelivery delivery = this.pendingDeliveries.stream().filter(d -> d.getId().equals(deliveryId))
                .findFirst().orElseThrow(() -> new DomainException());
        this.pendingDeliveries.remove(delivery);
        this.fulfilledDeliveriesQuantity++;
        this.pendingDeliveriesQuantity--;
        this.lastFulfilledDeliveryAt = OffsetDateTime.now();
    }

    public Iterable<AssignedDelivery> getPendingDeliveries() {
        return Collections.unmodifiableList(pendingDeliveries);
    }
}