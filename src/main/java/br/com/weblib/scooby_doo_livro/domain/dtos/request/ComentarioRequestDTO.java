package br.com.weblib.scooby_doo_livro.domain.dtos.request;

public record ComentarioRequestDTO(
        Long idLivro,
        String conteudo,
        Long idComentarioPai
) {}