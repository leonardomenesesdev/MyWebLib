package br.com.weblib.scooby_doo_livro.Repository;

import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    Boolean existsByTituloAndAutor(String tituloLivro, String autor);
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    List<Livro> findByCategoriasContaining(EnumCategoria categoria);
    List<Livro> findByAutorContainingIgnoreCase(String autor);
    @Query("SELECT l FROM Livro l WHERE :categoria MEMBER OF l.categorias")
    List<Livro> findByCategoria(@Param("categoria") EnumCategoria categoria);
    @Query(value = "SELECT l FROM Livro l LEFT JOIN FETCH l.categorias",
            countQuery = "SELECT count(l) FROM Livro l")
    Page<Livro> findAllLivrosComCategorias(Pageable pageable);
    List<Livro> findByAutorContainingIgnoreCaseOrTituloContainingIgnoreCase(String termo, String termo2);

    // --- NOVO MÉTODO ---
    @Modifying // Indica que é um UPDATE/DELETE
    @Transactional // Necessário para operações de modificação customizadas
    @Query("UPDATE Livro l SET l.avaliacao_media = :media WHERE l.id = :id")
    void atualizarMediaDoLivro(@Param("id") Long id, @Param("media") Double media);
}
