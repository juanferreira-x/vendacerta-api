package com.api.vendacerta.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.api.vendacerta.exception.InsufficientStockException;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários (sem Spring, sem banco) das regras que o {@link Product} protege sozinho.
 */
class ProductTest {

    private static final BigDecimal PRICE = new BigDecimal("10.00");

    @Test
    @DisplayName("Cria produto válido com os dados informados")
    void shouldCreateProductWithGivenData() {
        Product product = new Product("Caneca", PRICE, 5);

        assertThat(product.getName()).isEqualTo("Caneca");
        assertThat(product.getPrice()).isEqualByComparingTo("10.00");
        assertThat(product.getStockQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Recusa nome vazio ou longo demais")
    void shouldRejectBlankOrTooLongName() {
        String tooLongName = "a".repeat(Product.NAME_MAX_LENGTH + 1);

        assertThatThrownBy(() -> new Product(" ", PRICE, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Product(null, PRICE, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Product(tooLongName, PRICE, 1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Recusa preço nulo ou negativo")
    void shouldRejectNullOrNegativePrice() {
        assertThatThrownBy(() -> new Product("Caneca", null, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Product("Caneca", new BigDecimal("-0.01"), 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Recusa estoque inicial negativo")
    void shouldRejectNegativeInitialStock() {
        assertThatThrownBy(() -> new Product("Caneca", PRICE, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Abate a quantidade vendida do estoque")
    void shouldDecreaseStock() {
        Product product = new Product("Caneca", PRICE, 5);

        product.decreaseStock(3);

        assertThat(product.getStockQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Permite vender exatamente todo o estoque")
    void shouldAllowSellingTheWholeStock() {
        Product product = new Product("Caneca", PRICE, 5);

        product.decreaseStock(5);

        assertThat(product.getStockQuantity()).isZero();
    }

    @Test
    @DisplayName("Recusa venda maior que o estoque e não altera o estoque")
    void shouldRejectDecreaseWhenStockIsInsufficient() {
        Product product = new Product("Caneca", PRICE, 2);

        assertThatThrownBy(() -> product.decreaseStock(3)).isInstanceOf(InsufficientStockException.class);
        assertThat(product.getStockQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Recusa quantidade zero ou negativa (o bug antigo que aumentava o estoque)")
    void shouldRejectZeroOrNegativeQuantity() {
        Product product = new Product("Caneca", PRICE, 5);

        assertThatThrownBy(() -> product.decreaseStock(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> product.decreaseStock(-5)).isInstanceOf(IllegalArgumentException.class);
        assertThat(product.getStockQuantity()).isEqualTo(5);
    }
}
