package br.com.weblib.scooby_doo_livro.domain.model.Avaliacao;

public record AvaliacaoResponseDTO(Long avaliacaoId, Long livroId,
                                   String emailUsuario) {
}
