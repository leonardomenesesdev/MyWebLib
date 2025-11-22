package br.com.weblib.scooby_doo_livro.domain.model.Comentario;

import java.util.Date;

public record ComentarioResponseDTO(
        Long id,
        String conteudo,
        String nomeUsuario,
        Long idUsuario,
        Long idLivro,
        Date data
) {
    public ComentarioResponseDTO(Comentario comentario) {
        this(
                comentario.getId(),
                comentario.getConteudo(),
                comentario.getUsuario().getNome(),
                comentario.getUsuario().getId(),
                comentario.getLivro().getId(),
                comentario.getData()
        );
    }
}

