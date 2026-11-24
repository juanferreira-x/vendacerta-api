package com.api.vendacerta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.api.vendacerta.dto.SaleItemRequestDto;
import com.api.vendacerta.dto.SaleRequestDto;
import com.api.vendacerta.dto.SaleResponseDto;
import com.api.vendacerta.entity.Product;
import com.api.vendacerta.entity.Sale;
import com.api.vendacerta.exception.InsufficientStockException;
import com.api.vendacerta.exception.ResourceNotFoundException;
import com.api.vendacerta.repository.SaleRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Testes unitários do {@link SaleService}. Os colaboradores (repository e ProductService) são
 * "dublês" do Mockito: o teste controla o que eles devolvem e roda sem banco e sem Spring.
 */
@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private SaleService saleService;

    @Test
    @DisplayName("Cria a venda, abate o estoque e calcula o total")
    void shouldCreateSaleDecreasingStock() {
        Product mug = new Product("Caneca", new BigDecimal("39.90"), 10);
        when(productService.findEntityById(1L)).thenReturn(mug);
        when(saleRepository.save(any(Sale.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SaleResponseDto response = saleService.create(saleOf(new SaleItemRequestDto(1L, 3)));

        assertThat(mug.getStockQuantity()).isEqualTo(7);
        assertThat(response.total()).isEqualByComparingTo("119.70");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().productName()).isEqualTo("Caneca");
    }

    @Test
    @DisplayName("Recusa venda com estoque insuficiente e não salva nada")
    void shouldRejectSaleWhenStockIsInsufficient() {
        when(productService.findEntityById(1L)).thenReturn(new Product("Caneca", BigDecimal.TEN, 2));

        assertThatThrownBy(() -> saleService.create(saleOf(new SaleItemRequestDto(1L, 5))))
                .isInstanceOf(InsufficientStockException.class);
        verify(saleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Recusa venda com produto inexistente e não salva nada")
    void shouldRejectSaleWhenProductDoesNotExist() {
        when(productService.findEntityById(99L)).thenThrow(new ResourceNotFoundException("product.not-found", 99L));

        assertThatThrownBy(() -> saleService.create(saleOf(new SaleItemRequestDto(99L, 1))))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(saleRepository, never()).save(any());
    }

    private static SaleRequestDto saleOf(SaleItemRequestDto... items) {
        return new SaleRequestDto(List.of(items));
    }
}
