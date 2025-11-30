package br.com.weblib.scooby_doo_livro.repository;

import br.com.weblib.scooby_doo_livro.domain.dtos.response.UserDetailsDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    UserDetailsDTO findUserDetailsById(long id);
    List<UserDetailsDTO> findByNomeContainingIgnoreCase(String nome);
    boolean existsByEmail(String email);
    UserDetails findByEmail(String email);
}
