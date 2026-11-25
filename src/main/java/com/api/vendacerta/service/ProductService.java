package com.api.vendacerta.service;

import com.api.vendacerta.dto.PageResponseDto;
import com.api.vendacerta.dto.ProductRequestDto;
import com.api.vendacerta.dto.ProductResponseDto;
import com.api.vendacerta.entity.Product;
import com.api.vendacerta.exception.InvalidSortException;
import com.api.vendacerta.exception.ResourceNotFoundException;
import com.api.vendacerta.repository.ProductRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    /** Campos pelos quais o cliente pode ordenar a listagem (ex: {@code ?sort=price,desc}). */
    static final List<String> SORTABLE_FIELDS = List.of("id", "name", "price");

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

    /**
     * Lista os produtos uma página por vez. O banco devolve só as linhas da página pedida
     * ({@code LIMIT}/{@code OFFSET}), nunca a tabela inteira.
     *
     * @throws InvalidSortException se o cliente pedir ordenação por um campo fora de {@link #SORTABLE_FIELDS}
     */
    @Transactional(readOnly = true)
    public PageResponseDto<ProductResponseDto> findAll(Pageable pageable) {
        rejectUnknownSortFields(pageable.getSort());
        return PageResponseDto.from(productRepository.findAll(pageable), ProductResponseDto::fromEntity);
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

    /**
     * Allowlist (lista permitida) de ordenação: só aceita os campos de {@link #SORTABLE_FIELDS}.
     * Segurança por padrão: tudo que não está na lista é recusado com 400.
     */
    private void rejectUnknownSortFields(Sort sort) {
        for (Sort.Order order : sort) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new InvalidSortException(order.getProperty(), SORTABLE_FIELDS);
            }
        }
    }
}
