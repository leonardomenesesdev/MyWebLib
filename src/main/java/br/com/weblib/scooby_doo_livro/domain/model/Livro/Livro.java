package br.com.weblib.scooby_doo_livro.domain.model.Livro;

import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao.Avaliacao;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.Comentario; // Importe o Comentario
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import br.com.weblib.scooby_doo_livro.domain.model.interfaces.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnore; // Importante
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString; // Importante

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Livro implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 500)
    private String titulo;

    @Column(length = 500)
    private String autor;

    @Column(length = 1000)
    private String capa;

    private Integer ano;

    @Column(length = 2000)
    private String sinopse;

    @ElementCollection(targetClass = EnumCategoria.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "livro_categorias", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "categoria")
    private List<EnumCategoria> categorias;

    @Column(name = "avaliacao_media")
    private Double avaliacaoMedia = 0.0;

    // --- CONFIGURAÇÃO DO CASCADE (OPÇÃO A) ---

    // 1. Configuração para Avaliações
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Evita loop infinito no JSON
    @ToString.Exclude // Evita loop infinito no Log
    private List<Avaliacao> avaliacoes;

    // 2. Configuração para Comentários
    @OneToMany(mappedBy = "livro", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Evita loop infinito no JSON
    @ToString.Exclude // Evita loop infinito no Log
    private List<Comentario> comentarios;
}