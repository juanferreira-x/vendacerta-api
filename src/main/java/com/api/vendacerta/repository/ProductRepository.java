package com.api.vendacerta.repository;

import com.api.vendacerta.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso ao banco para {@link Product}. O Spring Data gera a implementação em tempo
 * de execução (save, findById, findAll, ...).
 */
public interface ProductRepository extends JpaRepository<Product, Long> {}
