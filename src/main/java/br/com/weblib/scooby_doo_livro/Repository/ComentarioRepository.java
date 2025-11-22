package br.com.weblib.scooby_doo_livro.Repository;

import br.com.weblib.scooby_doo_livro.domain.model.Comentario.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    Page<Comentario> findByLivroId(Long idLivro, Pageable pageable);
}
