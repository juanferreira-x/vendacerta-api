package com.api.vendacerta.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários de {@link Sale} e {@link SaleItem}: total, subtotal e o snapshot do preço.
 */
class SaleTest {

    @Test
    @DisplayName("Subtotal do item é preço unitário vezes quantidade")
    void shouldComputeItemSubtotal() {
        SaleItem item = new SaleItem(new Product("Caneca", new BigDecimal("39.90"), 10), 3);

        assertThat(item.getSubtotal()).isEqualByComparingTo("119.70");
    }

    @Test
    @DisplayName("Total da venda é a soma dos subtotais e o item aponta de volta para a venda")
    void shouldSumItemsIntoTotalAndLinkBothSides() {
        Sale sale = new Sale();
        SaleItem mug = new SaleItem(new Product("Caneca", new BigDecimal("39.90"), 10), 2);
        SaleItem notebook = new SaleItem(new Product("Caderno", new BigDecimal("34.50"), 10), 1);

        sale.addItem(mug);
        sale.addItem(notebook);

        assertThat(sale.getTotal()).isEqualByComparingTo("114.30");
        assertThat(sale.getItems()).containsExactly(mug, notebook);
        assertThat(mug.getSale()).isSameAs(sale);
    }

    @Test
    @DisplayName("Lista de itens é somente leitura: só addItem pode alterar")
    void shouldExposeItemsAsReadOnly() {
        Sale sale = new Sale();
        SaleItem item = new SaleItem(new Product("Caneca", BigDecimal.TEN, 10), 1);

        assertThatThrownBy(() -> sale.getItems().add(item)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Snapshot: o item guarda o preço do momento da venda")
    void shouldKeepPriceSnapshotAtSaleTime() {
        Product product = new Product("Caneca", new BigDecimal("39.90"), 10);

        SaleItem item = new SaleItem(product, 1);

        assertThat(item.getUnitPrice()).isEqualByComparingTo("39.90");
    }

    @Test
    @DisplayName("Recusa item sem produto ou com quantidade não positiva")
    void shouldRejectInvalidItem() {
        Product product = new Product("Caneca", BigDecimal.TEN, 10);

        assertThatThrownBy(() -> new SaleItem(null, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SaleItem(product, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
