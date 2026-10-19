package com.api.vendacerta.services;

import com.api.vendacerta.entities.Produto;
import com.api.vendacerta.dtos.ProdutoRequestDTO;
import com.api.vendacerta.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto cadastrarProduto(ProdutoRequestDTO dto){

    Produto produto = new Produto();

    produto.setNome(dto.nome());
    produto.setPreco(dto.preco());
    produto.setQuantidadeEstoque(dto.quantidadeEstoque());

        return produtoRepository.save(produto);
    }
}
