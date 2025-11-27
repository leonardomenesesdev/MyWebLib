package br.com.weblib.scooby_doo_livro.Repository;

import br.com.weblib.scooby_doo_livro.domain.model.Usuario.UserDetailsDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    UserDetailsDTO findUserDetailsById(long id);
    List<UserDetailsDTO> findByNomeContainingIgnoreCase(String nome);
    boolean existsByEmail(String email);
    UserDetails findByEmail(String email);
}
