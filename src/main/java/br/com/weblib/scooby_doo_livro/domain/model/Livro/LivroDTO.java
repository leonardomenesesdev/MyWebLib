package br.com.weblib.scooby_doo_livro.domain.model.Livro;

import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;

import java.util.List;

public record LivroDTO(
    Long id,
    String titulo,
    String autor,
    String capa,
    Integer ano,
    List<EnumCategoria> categorias
) {
    public LivroDTO(Livro livro) {
            this(
                    livro.getId(),
                    livro.getTitulo(),
                    livro.getAutor(),
                    livro.getCapa(),
                    livro.getAno(),
                    livro.getCategorias()
            );
        }
}
