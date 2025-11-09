package br.com.weblib.scooby_doo_livro.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comentario {
    private Long id;
    private Long idUsuario;
    private Long idLivro;
    private String conteudo;
    private Date data;

}
