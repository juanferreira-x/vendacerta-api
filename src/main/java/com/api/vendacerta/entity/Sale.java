package com.api.vendacerta.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Venda concluída e seus itens. Mapeada para a tabela {@code sales}.
 *
 * <p>Os itens são salvos junto com a venda ({@code cascade = ALL}) e só podem ser
 * adicionados por {@link #addItem(SaleItem)}, que mantém os dois lados do
 * relacionamento bidirecional sincronizados.</p>
 */
@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleItem> items = new ArrayList<>();

    /**
     * Adiciona um item à venda, liga o item de volta a ela e atualiza o total.
     */
    public void addItem(SaleItem item) {
        item.setSale(this);
        items.add(item);
        total = total.add(item.getSubtotal());
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Visão somente leitura: alterações devem passar por {@link #addItem(SaleItem)}
     * para o total continuar correto.
     */
    public List<SaleItem> getItems() {
        return Collections.unmodifiableList(items);
    }
}
