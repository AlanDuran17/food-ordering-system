package com.alanduran.payment.service.domain.entity;

import com.alanduran.domain.entity.AggregateRoot;
import com.alanduran.domain.valueobject.CustomerId;
import com.alanduran.domain.valueobject.Money;
import com.alanduran.payment.service.domain.valueobject.CreditEntryId;
import lombok.Getter;

@Getter
public class CreditEntry extends AggregateRoot<CreditEntryId> {

    private final CustomerId customerId;
    private Money totalCreditAmount;

    public void addCreditAmouint(Money amount) {
        totalCreditAmount = totalCreditAmount.add(amount);
    }

    public void subtractCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.subtract(amount);
    }

    private CreditEntry(Builder builder) {
        setId(builder.CreditEntryId);
        customerId = builder.customerId;
        totalCreditAmount = builder.totalCreditAmount;
    }


    public static final class Builder {
        private CreditEntryId CreditEntryId;
        private CustomerId customerId;
        private Money totalCreditAmount;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(CreditEntryId val) {
            CreditEntryId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder totalCreditAmount(Money val) {
            totalCreditAmount = val;
            return this;
        }

        public CreditEntry build() {
            return new CreditEntry(this);
        }
    }
}
