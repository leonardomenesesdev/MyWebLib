package br.com.weblib.scooby_doo_livro.Repository;

import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {


}
