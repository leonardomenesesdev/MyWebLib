package br.com.weblib.scooby_doo_livro.domain.dtos.response;


import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;

import java.util.List;

public record LivroResumoDTO(
        Long id,
        String titulo,
        String autor,
        String capa,
        Integer ano,
        Double avaliacaoMedia,
        List<EnumCategoria> categorias
) {
    // Construtor auxiliar que extrai dados da Entidade Livro
    public LivroResumoDTO(Livro livro) {
        this(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getCapa(),
                livro.getAno(),
                livro.getAvaliacaoMedia(),
                livro.getCategorias()
        );
    }
}