package br.com.weblib.scooby_doo_livro.domain.model.exceptions;

public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String message) {
        super(message);
    }
}
