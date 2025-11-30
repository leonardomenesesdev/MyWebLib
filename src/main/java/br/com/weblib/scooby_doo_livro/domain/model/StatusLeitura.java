package br.com.weblib.scooby_doo_livro.domain.model;

import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.interfaces.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
// Ajustamos os nomes das colunas na constraint para bater com o @JoinColumn abaixo
@Table(name = "status_leitura", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_usuario", "id_livro"})
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class StatusLeitura implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- MUDANÇA 1: De Long para Entidade Livro ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_livro", nullable = false)
    @JsonIgnoreProperties({"avaliacoes", "comentarios", "categorias", "sinopse"}) // Evita trazer o livro inteiro pesado
    private Livro livro;

    // --- MUDANÇA 2: De Long para Entidade Usuario ---
    // É este campo que o mappedBy="usuario" lá na classe Usuario está procurando!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnoreProperties({"avaliacoes", "comentarios", "role", "hashSenha"}) // Evita loop infinito
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumStatusLeitura statusLeitura;

    // Construtor ajustado para receber Entidades
    public StatusLeitura(Usuario usuario, Livro livro, EnumStatusLeitura statusLeitura) {
        this.usuario = usuario;
        this.livro = livro;
        this.statusLeitura = statusLeitura;
    }
}