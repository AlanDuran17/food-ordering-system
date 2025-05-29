package com.alanduran.restaurant.service.dataaccess.restaurant.outbox.entity;

import com.alanduran.domain.valueobject.OrderApprovalStatus;
import com.alanduran.outbox.OutboxStatus;
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
@Table(name = "order_outbox")
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
public class OrderOutboxEntity {
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
    private OutboxStatus outboxStatus;
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private OrderApprovalStatus approvalStatus;
    private int version;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderOutboxEntity that = (OrderOutboxEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
