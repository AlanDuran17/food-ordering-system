package order.service.dataaccess.customer.mapper;

import com.alanduran.domain.valueobject.CustomerId;
import com.alanduran.order.service.domain.entity.Customer;
import order.service.dataaccess.customer.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()));
    }
}
