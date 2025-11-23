package br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura;

import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.interfaces.Identifiable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
//um usuario so pode ter um status de leitura pra um livro
@Table(name = "status_leitura", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"idUsuario", "idLivro"})
})
public class StatusLeitura implements Identifiable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long idLivro;

    @Column
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    @Column
    private EnumStatusLeitura statusLeitura;

    // Construtor utilitário para facilitar criação
    public StatusLeitura(Long idUsuario, Long idLivro, EnumStatusLeitura statusLeitura) {
        this.idUsuario = idUsuario;
        this.idLivro = idLivro;
        this.statusLeitura = statusLeitura;
    }
}
