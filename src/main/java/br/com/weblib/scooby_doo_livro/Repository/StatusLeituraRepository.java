package br.com.weblib.scooby_doo_livro.Repository;

import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.StatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusLeituraRepository extends JpaRepository<StatusLeitura,Long> {
    Optional<StatusLeitura> findByIdUsuarioAndIdLivro(Long idUsuario, Long idLivro);
    @Query("SELECT l FROM Livro l WHERE l.id IN " +
            "(SELECT s.idLivro FROM StatusLeitura s WHERE s.idUsuario = :idUsuario AND s.statusLeitura = :status)")
    List<Livro> findLivrosPorStatusDoUsuario(@Param("idUsuario") Long idUsuario,
                                             @Param("status") EnumStatusLeitura status);
    long countByIdUsuarioAndStatusLeitura(Long idUsuario, EnumStatusLeitura statusLeitura);
}
