package com.alanduran.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Money {
    private final BigDecimal amount;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public boolean isGreaterThanZero() {
        return this.amount != null && this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money money) {
        return this.amount != null && this.amount.compareTo(money.getAmount()) > 0;
    }

    /**
     * Adds the given amount to this one.
     *
     * @param money     The amount to add
     * @return          A new Money instance with the result
     */
    public Money add(Money money) {
        return new Money(setScale(this.amount.add(money.getAmount())));
    }


    /**
     * Subtracts the given amount from this one.
     *
     * @param money     The amount to subtract
     * @return          A new Money instance with the result
     */
    public Money subtract(Money money) {
        return new Money(setScale(this.amount.subtract(money.getAmount())));
    }

    /**
     * Multiplies the given amount from this one.
     *
     * @param multiplier    The amount to multiply
     * @return              A new Money instance with the result
     */
    public Money multiply(int multiplier) {
        return new Money(setScale(this.amount.multiply(BigDecimal.valueOf(multiplier))));
    }

    /**
     * Sets the maximum decimal scale for input and returns it
     * @param input     Original value
     * @return          Result
     */
    private BigDecimal setScale(BigDecimal input) {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }
}
