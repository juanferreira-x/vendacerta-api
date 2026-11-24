package com.api.vendacerta.service;

import com.api.vendacerta.dto.ProductRequestDto;
import com.api.vendacerta.dto.ProductResponseDto;
import com.api.vendacerta.entity.Product;
import com.api.vendacerta.exception.ResourceNotFoundException;
import com.api.vendacerta.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regras de negócio de produtos.
 *
 * <p>Os métodos públicos devolvem DTOs para a API; {@link #findEntityById(Long)} é o único
 * que devolve a entidade, para uso interno de outros services.</p>
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponseDto create(ProductRequestDto request) {
        Product product = new Product(request.name(), request.price(), request.stockQuantity());

        Product savedProduct = productRepository.save(product);
        return ProductResponseDto.fromEntity(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponseDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDto findById(Long id) {
        return ProductResponseDto.fromEntity(findEntityById(id));
    }

    /**
     * Uso INTERNO (ex: {@link SaleService}). Devolve a entidade gerenciada pelo JPA para que
     * alterações como abater estoque sejam salvas automaticamente pelo dirty checking.
     * Nunca devolva esta entidade para um controller.
     *
     * @throws ResourceNotFoundException se nenhum produto tiver o id informado
     */
    public Product findEntityById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product.not-found", id));
    }
}
