package br.com.weblib.scooby_doo_livro.domain.model;

import br.com.weblib.scooby_doo_livro.domain.model.interfaces.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnore; // Importante
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode; // Importante
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Performance
public class Comentario implements Identifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_livro")
    private Livro livro;

    @Column(columnDefinition = "TEXT") // Dica: Comentários podem ser longos
    private String conteudo;

    private Date data;

    // --- Autorreferência (Pai) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comentario_pai")
    @JsonIgnoreProperties({"pai", "livro", "usuario", "respostas"})
    private Comentario pai;

    // --- Autorreferência (Filhos/Respostas) - NOVO ---
    // Isso permite que o Hibernate delete as respostas quando o pai for deletado
    @OneToMany(mappedBy = "pai", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Ignora lista de filhos na serialização padrão para evitar peso excessivo
    @ToString.Exclude // Evita loop no log
    private List<Comentario> respostas;
}