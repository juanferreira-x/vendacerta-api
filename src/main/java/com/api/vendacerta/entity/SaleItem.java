package com.api.vendacerta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Uma linha da venda: qual produto, quantas unidades e por qual preço.
 * Mapeado para a tabela {@code sale_items}.
 *
 * <p>O {@code unitPrice} é um retrato histórico copiado do produto no momento da venda:
 * mudanças futuras de preço nunca alteram vendas passadas.</p>
 */
@Entity
@Table(name = "sale_items")
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    protected SaleItem() {
    }

    /**
     * Cria o item congelando o preço atual do produto como preço unitário.
     */
    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Sale getSale() {
        return sale;
    }

    void setSale(Sale sale) {
        this.sale = sale;
    }

    public Product getProduct() {
        return product;
    }
}
