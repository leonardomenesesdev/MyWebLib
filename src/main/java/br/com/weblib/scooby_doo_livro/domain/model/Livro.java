package br.com.weblib.scooby_doo_livro.domain.model;

import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import br.com.weblib.scooby_doo_livro.domain.model.interfaces.Identifiable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Livro implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)  // Aumenta limite para títulos longos
    private String titulo;

    @Column(length = 500)  // Aumenta limite para múltiplos autores
    private String autor;

    @Column(length = 1000) // URLs de imagem podem ser longas
    private String capa;

    private Integer ano;

    @Column(length = 2000) // Sinopses costumam ser longas
    private String sinopse;

    private Double avaliacao_media;

    @ElementCollection(targetClass = EnumCategoria.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "livro_categorias", joinColumns = @JoinColumn(name = "livro_id"))
    @Column(name = "categoria")
    private List<EnumCategoria> categorias;
}