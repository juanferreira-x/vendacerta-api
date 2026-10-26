package com.api.vendacerta.repositories;

import com.api.vendacerta.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaRepository extends JpaRepository<Venda, Long> {
}
