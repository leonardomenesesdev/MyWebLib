package br.com.weblib.scooby_doo_livro.domain.model.Avaliacao;

import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.interfaces.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Avaliacao implements Identifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_livro")
    private Livro livro;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    private Integer nota;

}
