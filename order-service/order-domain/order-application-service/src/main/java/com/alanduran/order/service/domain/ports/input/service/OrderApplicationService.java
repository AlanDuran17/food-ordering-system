package com.alanduran.order.service.domain.ports.input.service;

import com.alanduran.order.service.domain.dto.create.CreateOrderCommand;
import com.alanduran.order.service.domain.dto.create.CreateOrderResponse;
import com.alanduran.order.service.domain.dto.track.TrackOrderQuery;
import com.alanduran.order.service.domain.dto.track.TrackOrderResponse;
import jakarta.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);

    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}
