package com.alanduran.domain.entity;

import lombok.Data;

@Data
public class BaseEntity<ID> {
    private ID id;
}
