package com.example.attestationspring.repositories;

import com.example.attestationspring.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository <Category,Integer>{
    CategoryRepository findByName(String name);
}
