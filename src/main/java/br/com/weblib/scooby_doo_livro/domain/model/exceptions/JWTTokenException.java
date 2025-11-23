package br.com.weblib.scooby_doo_livro.domain.model.exceptions;

public class JWTTokenException extends RuntimeException {
    public JWTTokenException(String message) {
        super(message);
    }
}
