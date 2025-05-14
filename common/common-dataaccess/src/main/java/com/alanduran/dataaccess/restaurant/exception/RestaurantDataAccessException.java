package com.alanduran.dataaccess.restaurant.exception;

public class RestaurantDataAccessException extends RuntimeException {
    public RestaurantDataAccessException(String message) {
        super(message);
    }

    public RestaurantDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
