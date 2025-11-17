package br.com.weblib.scooby_doo_livro.Repository;

import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    Boolean existsByTituloAndAutor(String tituloLivro, String autor);
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    List<Livro> findByCategoriasContaining(EnumCategoria categoria);
    List<Livro> findByAutorContainingIgnoreCase(String autor);
    @Query("SELECT l FROM Livro l WHERE :categoria MEMBER OF l.categorias")
    List<Livro> findByCategoria(@Param("categoria") EnumCategoria categoria);
}
