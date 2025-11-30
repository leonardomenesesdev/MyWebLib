package br.com.weblib.scooby_doo_livro.domain.dtos.response;

import lombok.Data;

import java.time.Instant;

@Data
public class ApiErrorResponse {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public ApiErrorResponse(Integer status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}
