package br.com.weblib.scooby_doo_livro.Repository;

import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
//    Optional<Avaliacao> findByLivroAndUsuario(Livro livro, Usuario usuario);
//
//    Double calcularMediaPorLivro(Long idLivro);
//    ArrayList<Avaliacao> findByLivro(Livro livro);
}
