package br.com.weblib.scooby_doo_livro.domain.model.exceptions;

public class LivroInvalidoParaFavoritarException extends RuntimeException {
    public LivroInvalidoParaFavoritarException(String message) {
        super(message);
    }
}
