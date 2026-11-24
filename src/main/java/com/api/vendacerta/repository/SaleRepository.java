package com.api.vendacerta.repository;

import com.api.vendacerta.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso ao banco para {@link Sale}. Salvar uma venda também salva seus itens (cascade).
 */
public interface SaleRepository extends JpaRepository<Sale, Long> {}
