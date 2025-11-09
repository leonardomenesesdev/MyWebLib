package br.com.weblib.scooby_doo_livro.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Avaliacao {
    private Long id;
    private Long idLivro;
    private Long idUsuario;
    private Integer nota;

}
