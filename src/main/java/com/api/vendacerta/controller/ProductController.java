package com.api.vendacerta.controller;

import com.api.vendacerta.dto.PageResponseDto;
import com.api.vendacerta.dto.ProductRequestDto;
import com.api.vendacerta.dto.ProductResponseDto;
import com.api.vendacerta.service.ProductService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Endpoints HTTP de produtos. Cuida só do HTTP e delega as regras ao {@link ProductService}.
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Cria um produto. Devolve 201 com o header {@code Location} apontando para o novo recurso.
     */
    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody ProductRequestDto request) {
        ProductResponseDto createdProduct = productService.create(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProduct.id())
                .toUri();

        return ResponseEntity.created(location).body(createdProduct);
    }

    /**
     * Lista os produtos paginados: {@code ?page=0&size=20&sort=price,desc}.
     * Sem parâmetros, devolve a primeira página com 20 itens ordenados por id.
     * O tamanho máximo de página (50) é imposto pela configuração {@code spring.data.web.pageable}.
     */
    @GetMapping
    public ResponseEntity<PageResponseDto<ProductResponseDto>> findAll(
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
}
