package br.com.weblib.scooby_doo_livro.domain.model.Usuario;

import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.EstatisticasDTO;

public record UserProfileResponseDTO(
        Long id,
        String nome,
        String email,
        String dataCadastro,
        EstatisticasDTO estatisticas
) {
    // Construtor auxiliar para facilitar a montagem
    public UserProfileResponseDTO(Usuario usuario, EstatisticasDTO estatisticas) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                "01/01/2023", // TODO: Pegar de usuario.getDataCadastro() se existir
                estatisticas
        );
    }
}