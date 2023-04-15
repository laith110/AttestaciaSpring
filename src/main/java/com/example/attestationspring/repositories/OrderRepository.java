package com.example.attestationspring.repositories;

import com.example.attestationspring.models.Order;
import com.example.attestationspring.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {


    List<Order> findByPerson(Person person);


//    @Query(value = "select * from orders where (lower(number) like %?1)", nativeQuery = true)
List<Order> findByNumberEndingWith(String search);




}
