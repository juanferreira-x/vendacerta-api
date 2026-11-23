package com.api.vendacerta.services;

import com.api.vendacerta.dtos.ItemVendaRequestDTO;
import com.api.vendacerta.dtos.VendaRequestDTO;
import com.api.vendacerta.dtos.VendaResponseDTO;
import com.api.vendacerta.entities.ItemVenda;
import com.api.vendacerta.entities.Produto;
import com.api.vendacerta.entities.Venda;
import com.api.vendacerta.exceptions.EstoqueInsuficienteException;
import com.api.vendacerta.repositories.ProdutoRepository;
import com.api.vendacerta.repositories.VendaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;

    public VendaService(VendaRepository vendaRepository, ProdutoService produtoService) {
        this.vendaRepository = vendaRepository;
        this.produtoService = produtoService;
    }

    @Transactional
    public VendaResponseDTO realizarVenda(VendaRequestDTO dto) {
        Venda venda = new Venda();
        BigDecimal valorTotal = new BigDecimal("0.00");


        for (ItemVendaRequestDTO itemDTO : dto.itens()) {
            Produto produto = produtoService.buscarEntidadePorId(itemDTO.produtoId());


            if (produto.getQuantidadeEstoque() < itemDTO.quantidade()) {
                throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome() + " | Disponível: " + produto.getQuantidadeEstoque() + " | Solicitado: " + itemDTO.quantidade());
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

        Venda vendaSalva = vendaRepository.save(venda);
        return VendaResponseDTO.fromEntity(vendaSalva);
    }
}
