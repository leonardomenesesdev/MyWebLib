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
public interface StatusLeituraRepository extends JpaRepository<StatusLeitura, Long> {

    // Spring Data navega: StatusLeitura -> Usuario -> Id
    Optional<StatusLeitura> findByUsuarioIdAndLivroId(Long usuarioId, Long livroId);

    // Query otimizada: Navega direto pelo objeto 's.livro'
    @Query("SELECT s.livro FROM StatusLeitura s WHERE s.usuario.id = :idUsuario AND s.statusLeitura = :status")
    List<Livro> findLivrosPorStatusDoUsuario(@Param("idUsuario") Long idUsuario,
                                             @Param("status") EnumStatusLeitura status);

    // Contagem usando a navegação pelo ID do objeto Usuario
    long countByUsuarioIdAndStatusLeitura(Long usuarioId, EnumStatusLeitura statusLeitura);
}