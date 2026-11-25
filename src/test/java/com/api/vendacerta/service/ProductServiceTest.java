package com.api.vendacerta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.api.vendacerta.dto.PageResponseDto;
import com.api.vendacerta.dto.ProductResponseDto;
import com.api.vendacerta.entity.Product;
import com.api.vendacerta.exception.InvalidSortException;
import com.api.vendacerta.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Testes unitários da listagem paginada do {@link ProductService}, com o repository como dublê.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Converte a página de entidades numa página de DTOs com os totais")
    void shouldReturnPageOfDtos() {
        PageRequest request = PageRequest.of(0, 2, Sort.by("price"));
        List<Product> products = List.of(
                new Product("Caneca", new BigDecimal("39.90"), 5), new Product("Vela", new BigDecimal("49.00"), 3));
        when(productRepository.findAll(request)).thenReturn(new PageImpl<>(products, request, 5));

        PageResponseDto<ProductResponseDto> page = productService.findAll(request);

        assertThat(page.content()).extracting(ProductResponseDto::name).containsExactly("Caneca", "Vela");
        assertThat(page.page()).isZero();
        assertThat(page.size()).isEqualTo(2);
        assertThat(page.totalElements()).isEqualTo(5);
        assertThat(page.totalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("Recusa ordenação por campo fora da lista permitida, sem consultar o banco")
    void shouldRejectUnknownSortFieldWithoutQueryingDatabase() {
        PageRequest request = PageRequest.of(0, 20, Sort.by("version"));

        assertThatThrownBy(() -> productService.findAll(request)).isInstanceOf(InvalidSortException.class);
        verify(productRepository, never()).findAll(any(PageRequest.class));
    }
}
