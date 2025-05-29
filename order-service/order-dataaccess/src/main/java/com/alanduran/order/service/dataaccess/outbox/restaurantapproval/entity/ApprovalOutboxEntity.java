package com.alanduran.order.service.dataaccess.outbox.restaurantapproval.entity;

import com.alanduran.domain.valueobject.OrderStatus;
import com.alanduran.outbox.OutboxStatus;
import com.alanduran.saga.SagaStatus;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_approval_outbox")
@Entity
@TypeDefs(
    value = {@TypeDef(
            name = "pgsql_enum",
            typeClass = PostgreSQLEnumType.class
    ),
    @TypeDef(
            name = "json",
            typeClass = JsonType.class
    )}
)
public class ApprovalOutboxEntity {

    @Id
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private String payload;
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private SagaStatus sagaStatus;
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private OrderStatus orderStatus;
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private OutboxStatus outboxStatus;
    @Version
    private int version;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApprovalOutboxEntity that = (ApprovalOutboxEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}