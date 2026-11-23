package com.api.vendacerta.service;

import com.api.vendacerta.dto.SaleItemRequestDto;
import com.api.vendacerta.dto.SaleRequestDto;
import com.api.vendacerta.dto.SaleResponseDto;
import com.api.vendacerta.entity.Product;
import com.api.vendacerta.entity.Sale;
import com.api.vendacerta.entity.SaleItem;
import com.api.vendacerta.exception.InsufficientStockException;
import com.api.vendacerta.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio de vendas.
 */
@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;

    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    /**
     * Cria uma venda, abatendo o estoque de cada produto vendido.
     *
     * <p>Roda numa única transação: se qualquer item falhar (produto inexistente ou estoque
     * insuficiente), tudo é desfeito e nenhum estoque muda. O estoque abatido é salvo pelo
     * dirty checking, já que os produtos são entidades gerenciadas pelo JPA.</p>
     *
     * @throws com.api.vendacerta.exception.ResourceNotFoundException se um produto não existir
     * @throws InsufficientStockException                             se um produto tiver menos unidades que o pedido
     */
    @Transactional
    public SaleResponseDto create(SaleRequestDto request) {
        Sale sale = new Sale();

        for (SaleItemRequestDto itemRequest : request.items()) {
            Product product = productService.findEntityById(itemRequest.productId());
            int requestedQuantity = itemRequest.quantity();

            product.decreaseStock(requestedQuantity);
            sale.addItem(new SaleItem(product, requestedQuantity));
        }

        Sale savedSale = saleRepository.save(sale);
        return SaleResponseDto.fromEntity(savedSale);
    }
}
