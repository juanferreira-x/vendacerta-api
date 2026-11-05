package com.api.vendacerta.services;

import com.api.vendacerta.dtos.ItemVendaRequestDTO;
import com.api.vendacerta.dtos.VendaRequestDTO;
import com.api.vendacerta.entities.ItemVenda;
import com.api.vendacerta.entities.Produto;
import com.api.vendacerta.entities.Venda;
import com.api.vendacerta.repositories.ProdutoRepository;
import com.api.vendacerta.repositories.VendaRepository;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;

public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;

    public VendaService(VendaRepository vendaRepository, ProdutoService produtoService) {
        this.vendaRepository = vendaRepository;
        this.produtoService = produtoService;
    }

    @Transactional
    public Venda realizarVenda(VendaRequestDTO dto) {
        Venda venda = new Venda();
        BigDecimal valorTotal = new BigDecimal("0.00");


        for (ItemVendaRequestDTO itemDTO : dto.itens()) {
            Produto produto = produtoService.buscarProdutosPorID(itemDTO.produtoId());


            if (produto.getQuantidadeEstoque() < itemDTO.quantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDTO.quantidade());

            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setProduto(produto);
            itemVenda.setQuantidade(itemDTO.quantidade());
            itemVenda.setPrecoUnitario(produto.getPreco());
            itemVenda.setVenda(venda);

            BigDecimal subtotal = produto.getPreco().multiply(new BigDecimal(itemDTO.quantidade()));
            valorTotal = valorTotal.add(subtotal);

            venda.getItens().add(itemVenda);
        }
        venda.setTotal(valorTotal);

        return vendaRepository.save(venda);
    }
}
