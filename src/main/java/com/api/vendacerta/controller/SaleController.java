package com.api.vendacerta.controller;

import com.api.vendacerta.dto.SaleRequestDto;
import com.api.vendacerta.dto.SaleResponseDto;
import com.api.vendacerta.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints HTTP de vendas. Cuida só do HTTP e delega as regras ao {@link SaleService}.
 */
@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @PostMapping
    public ResponseEntity<SaleResponseDto> create(@Valid @RequestBody SaleRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.create(request));
    }
}
