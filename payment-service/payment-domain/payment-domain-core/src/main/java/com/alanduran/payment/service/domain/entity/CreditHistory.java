package com.alanduran.payment.service.domain.entity;

import com.alanduran.domain.entity.AggregateRoot;
import com.alanduran.domain.valueobject.CustomerId;
import com.alanduran.domain.valueobject.Money;
import com.alanduran.payment.service.domain.valueobject.CreditHistoryId;
import com.alanduran.payment.service.domain.valueobject.TransactionType;
import lombok.Getter;

@Getter
public class CreditHistory extends AggregateRoot<CreditHistoryId> {

    private final CustomerId customerId;
    private final Money amount;
    private final TransactionType transactionType;

    private CreditHistory(Builder builder) {
        setId(builder.creditHistoryId);
        customerId = builder.customerId;
        amount = builder.amount;
        transactionType = builder.transactionType;
    }


    public static final class Builder {
        private CreditHistoryId creditHistoryId;
        private CustomerId customerId;
        private Money amount;
        private TransactionType transactionType;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder id(CreditHistoryId val) {
            creditHistoryId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder amount(Money val) {
            amount = val;
            return this;
        }

        public Builder transactionType(TransactionType val) {
            transactionType = val;
            return this;
        }

        public CreditHistory build() {
            return new CreditHistory(this);
        }
    }
}
