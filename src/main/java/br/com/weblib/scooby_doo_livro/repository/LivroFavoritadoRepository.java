package br.com.weblib.scooby_doo_livro.repository;

import br.com.weblib.scooby_doo_livro.domain.model.LivroFavoritado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroFavoritadoRepository extends JpaRepository<LivroFavoritado,Long> {
    boolean existsByUsuarioIdAndLivroId(Long idUsuario, Long idLivro);

    Optional<LivroFavoritado> findByUsuarioIdAndLivroId(Long idUsuario, Long idLivro);

    // Útil para contagens no perfil
    long countByUsuarioId(Long idUsuario);

    List<LivroFavoritado> findAllByUsuarioId(Long idUsuario);

}
