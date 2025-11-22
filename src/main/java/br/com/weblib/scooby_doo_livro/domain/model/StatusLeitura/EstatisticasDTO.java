package br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura;

public record EstatisticasDTO(
        long queroLer,
        long lendo,
        long lido,
        long favoritos,
        long avaliacoes
) {}
