package br.com.weblib.scooby_doo_livro.controller.exceptions;

import br.com.weblib.scooby_doo_livro.domain.model.ApiErrorResponse;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
                "Livro inválido",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
