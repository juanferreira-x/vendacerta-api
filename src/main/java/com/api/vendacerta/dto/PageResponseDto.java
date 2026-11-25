package com.api.vendacerta.dto;

import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Page;

/**
 * "Envelope" de uma página de resultados: os itens da página atual mais as informações para o
 * cliente montar a navegação (próxima, anterior, total).
 *
 * <p>É um formato NOSSO, de propósito: o JSON do {@link Page} do Spring não é estável entre versões.
 * Com este record, o contrato com os clientes (Web, Mobile, Sistemas) só muda quando nós quisermos.</p>
 *
 * @param content       os itens desta página
 * @param page          número da página atual (começa em 0)
 * @param size          quantidade máxima de itens por página
 * @param totalElements total de itens em todas as páginas
 * @param totalPages    total de páginas
 * @param <T>           o tipo dos itens (ex: {@link ProductResponseDto})
 */
public record PageResponseDto<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    /**
     * Converte uma página do Spring (com entidades) numa página nossa (com DTOs).
     *
     * @param page   página devolvida pelo repository, com entidades (ex: Product)
     * @param mapper função que converte UMA entidade em UM DTO (ex: ProductResponseDto::fromEntity)
     * @param <E>    tipo da entidade
     * @param <T>    tipo do DTO
     */
    public static <E, T> PageResponseDto<T> from(Page<E> page, Function<E, T> mapper) {

        List<T> content = page.getContent().stream().map(mapper).toList();

        return new PageResponseDto<>(
                content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
