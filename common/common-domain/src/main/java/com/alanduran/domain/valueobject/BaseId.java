package com.alanduran.domain.valueobject;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseId<T> {
    private final T value;
}
