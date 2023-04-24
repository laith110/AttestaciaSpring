package com.example.attestationspring.repositories;

import com.example.attestationspring.models.Order;

import com.example.attestationspring.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {


    List<Order> findByPerson(Person person);


//    @Query(value = "select * from orders where (lower(number) like %?1)", nativeQuery = true)
List<Order> findByNumberEndingWith(String search);

    @Modifying
    @Query(value = "delete from orders where id=?1", nativeQuery = true)
    void deletePersonById(int id);



}
