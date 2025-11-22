package br.com.weblib.scooby_doo_livro.domain.model.Comentario;

public record ComentarioRequestDTO(
        Long idLivro,
        String conteudo
) {}