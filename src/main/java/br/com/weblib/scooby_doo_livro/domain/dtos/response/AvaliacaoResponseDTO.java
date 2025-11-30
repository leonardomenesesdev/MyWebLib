package br.com.weblib.scooby_doo_livro.domain.dtos.response;

public record AvaliacaoResponseDTO(Long avaliacaoId, Long livroId,
                                   String emailUsuario, Integer nota) {
}
