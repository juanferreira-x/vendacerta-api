package com.api.vendacerta.services;

import com.api.vendacerta.dtos.ProdutoResponseDTO;
import com.api.vendacerta.entities.Produto;
import com.api.vendacerta.dtos.ProdutoRequestDTO;
import com.api.vendacerta.exceptions.ResourceNotFoundException;
import com.api.vendacerta.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public ProdutoResponseDTO cadastrarProduto(ProdutoRequestDTO dto) {

        Produto produto = new Produto();

        produto.setNome(dto.nome());
        produto.setPreco(dto.preco());
        produto.setQuantidadeEstoque(dto.quantidadeEstoque());

        Produto produtoSalvo = produtoRepository.save(produto);
        return ProdutoResponseDTO.fromEntity(produtoSalvo);
    }

    public List<ProdutoResponseDTO> listarProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        return produtos.stream().map(ProdutoResponseDTO::fromEntity).toList();
    }

    public Produto buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id + "."));
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = buscarEntidadePorId(id);
        return ProdutoResponseDTO.fromEntity(produto);

    }
}
