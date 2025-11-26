package br.com.weblib.scooby_doo_livro.domain.model.LivroFavoritado;

import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.interfaces.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(
        name = "livro_favoritado",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_livro", "id_usuario"})
)
public class LivroFavoritado implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_livro")
    @JsonIgnoreProperties({"favoritos", "avaliacoes", "comentarios"})
    private Livro livro;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @JsonIgnoreProperties({"favoritos"})
    private Usuario usuario;

    private LocalDateTime dataAdicao;


}
