package br.com.weblib.scooby_doo_livro.domain.model;

import br.com.weblib.scooby_doo_livro.domain.model.interfaces.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode; // Importante
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Performance: Igualdade só pelo ID
public class Avaliacao implements Identifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Apenas o ID define se duas avaliações são iguais
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_livro")
    private Livro livro;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private Integer nota;
}