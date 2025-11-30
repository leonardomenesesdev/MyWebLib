package br.com.weblib.scooby_doo_livro.repository;

import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao;
import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByLivroAndUsuario(Livro livro, Usuario usuario);
    ArrayList<Avaliacao> findByLivro(Livro livro);
    @Query("SELECT COALESCE(AVG(a.nota), 0.0) FROM Avaliacao a WHERE a.livro = :livro")
    Double obterMediaPorLivro(@Param("livro") Livro livro);
    long countByUsuarioId(Long usuarioId);
    @Query("SELECT a FROM Avaliacao a WHERE a.livro.id = :livroId AND a.usuario.id = :usuarioId")
    Optional<Avaliacao> findByLivroIdAndUsuarioId(@Param("livroId") Long livroId, @Param("usuarioId") Long usuarioId);
}
