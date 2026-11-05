package com.api.vendacerta.controllers;

import com.api.vendacerta.dtos.VendaRequestDTO;
import com.api.vendacerta.entities.Venda;
import com.api.vendacerta.services.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<Venda> realizarVenda(@Valid @RequestBody VendaRequestDTO dto) {
        Venda vendaSalva = vendaService.realizarVenda(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaSalva);
    }
}
