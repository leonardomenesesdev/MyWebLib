package br.com.weblib.scooby_doo_livro.domain.model.Livro;

import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;

import java.util.List;

public record LivroRequestDTO(
        String titulo,
        String autor,
        String capa,
        Integer ano,
        List<EnumCategoria> categorias
) {}
