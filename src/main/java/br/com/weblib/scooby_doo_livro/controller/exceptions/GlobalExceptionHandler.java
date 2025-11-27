package br.com.weblib.scooby_doo_livro.controller.exceptions;

import br.com.weblib.scooby_doo_livro.domain.model.ApiErrorResponse;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.JWTTokenException;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.LivroInvalidoParaFavoritarException;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleRecursoNaoEncontrado(
            RecursoNaoEncontradoException e,
            HttpServletRequest request
    ) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Dados não foram encontrados.",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        br.com.weblib.scooby_doo_livro.domain.model.ApiErrorResponse errorResponse = new br.com.weblib.scooby_doo_livro.domain.model.ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Entrada inválida",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(JWTTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleJWTTokenException(
            JWTTokenException e,
            HttpServletRequest request
    ) {
        br.com.weblib.scooby_doo_livro.domain.model.ApiErrorResponse errorResponse = new br.com.weblib.scooby_doo_livro.domain.model.ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Erro de autenticação.",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(LivroInvalidoParaFavoritarException.class)
    public ResponseEntity<ApiErrorResponse> handleLivroInvalidoParaFavoritarException(
            LivroInvalidoParaFavoritarException e,
            HttpServletRequest request
    ) {
        br.com.weblib.scooby_doo_livro.domain.model.ApiErrorResponse errorResponse = new br.com.weblib.scooby_doo_livro.domain.model.ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Falha ao favoritar o livro.",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


}
