package br.com.weblib.scooby_doo_livro.repository;

import br.com.weblib.scooby_doo_livro.domain.model.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    Page<Comentario> findByLivroId(Long idLivro, Pageable pageable);
}
