package com.example.attestationspring.services;
import com.example.attestationspring.models.Order;
import com.example.attestationspring.repositories.OrderRepository;
import com.example.attestationspring.repositories.PersonRepository;
import com.example.attestationspring.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PersonRepository personRepository;


    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, PersonRepository personRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.personRepository = personRepository;
    }

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    @Transactional
    public void deleteOrder(int id){
        orderRepository.deleteById(id);
    }
    @Transactional
    public void editStatusOrder(int id, Order order){
        order.setId(id);
        orderRepository.save(order);
    }


}