package com.api.vendacerta.entity;

import com.api.vendacerta.exception.InsufficientStockException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Produto disponível para venda. Mapeado para a tabela {@code products}.
 *
 * <p>Modelo interno: nunca atravessa a fronteira HTTP. Os clientes recebem
 * {@link com.api.vendacerta.dto.ProductResponseDto} no lugar.</p>
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stockQuantity;

    protected Product() {
    }

    /**
     * Cria um produto já válido: o objeto nunca existe com dados inválidos,
     * mesmo que seja criado fora do fluxo HTTP (que já é validado pelo DTO).
     *
     * @throws IllegalArgumentException se o preço for nulo/negativo ou o estoque for negativo
     */
    public Product(String name, BigDecimal price, int stockQuantity) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("O preço não pode ser nulo nem negativo.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("O estoque inicial não pode ser negativo.");
        }
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Abate a quantidade vendida do estoque.
     * A regra mora aqui (e não no service) para que o estoque nunca fique inválido,
     * não importa quem chame: venda hoje, outras operações no futuro.
     *
     * @param quantity quantidade a abater (já validada como positiva no DTO)
     * @throws InsufficientStockException se não houver unidades suficientes
     */
    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade a abater deve ser positiva.");
        }
        if (stockQuantity < quantity) {
            throw new InsufficientStockException(name, stockQuantity, quantity);
        }
        stockQuantity -= quantity;
    }
}
