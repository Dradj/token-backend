package com.zraj.tokenbackend.repository;

import com.zraj.tokenbackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
