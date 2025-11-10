package br.com.weblib.scooby_doo_livro;

import br.com.weblib.scooby_doo_livro.AlunoRepository.UsuarioRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ScoobyDooLivroApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScoobyDooLivroApplication.class, args);
	}
    @Bean
    CommandLineRunner init(UsuarioRepository usuarioRepository) {
        return args -> {
            Usuario u = new Usuario();
            u.setNome("Leonardo");
            u.setEmail("leo@teste.com");
            u.setHashSenha("1234");
            usuarioRepository.save(u);
        };
    }

}
