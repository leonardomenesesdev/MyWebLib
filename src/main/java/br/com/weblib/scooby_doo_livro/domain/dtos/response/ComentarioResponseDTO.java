package br.com.weblib.scooby_doo_livro.domain.dtos.response;

import br.com.weblib.scooby_doo_livro.domain.model.Comentario;

import java.util.Date;

public record ComentarioResponseDTO(
        Long id,
        String conteudo,
        String nomeUsuario,
        Long idUsuario,
        Long idLivro,
        Date data,
        Long idComentarioPai
) {
    public ComentarioResponseDTO(Comentario comentario) {
        this(
                comentario.getId(),
                comentario.getConteudo(),
                comentario.getUsuario().getNome(),
                comentario.getUsuario().getId(),
                comentario.getLivro().getId(),
                comentario.getData(),
                comentario.getPai() != null ? comentario.getPai().getId() : null // se tiver pai, manda o ID
        );
    }
}

