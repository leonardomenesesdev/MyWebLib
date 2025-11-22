package br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura;

import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusDTO(
        @NotNull Long idLivro,
        @NotNull EnumStatusLeitura status
) {}