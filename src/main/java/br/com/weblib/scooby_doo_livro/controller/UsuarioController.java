package br.com.weblib.scooby_doo_livro.controller;

import br.com.weblib.scooby_doo_livro.domain.model.Usuario.UserDetailsDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.UserProfileResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UserDetailsDTO>> listarTodos() {
        List<UserDetailsDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    // GET /user/{id} -> busca por id
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDTO> buscarPorId(@PathVariable Long id) {
        UserDetailsDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id,
                                             @RequestBody Usuario usuario) {
        logger.info("Recebida requisição para atualizar usuário ID: {}", id);

        Usuario atualizado = usuarioService.atualizar(id, usuario);

        return ResponseEntity.ok(atualizado);
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<UserProfileResponseDTO> buscarPerfilCompleto(@PathVariable Long id) {
        // Chama o metodo novo do service
        UserProfileResponseDTO perfil = usuarioService.buscarPerfilCompleto(id);

        return ResponseEntity.ok(perfil);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<UserDetailsDTO>> buscarPorNome(@PathVariable String nome) {
        List<UserDetailsDTO> usuariosFiltradosPorNome =
                usuarioService.buscarUsuarioPorNome(nome);

        return ResponseEntity.ok(usuariosFiltradosPorNome);
    }

    // DELETE /user/{id} -> Remove usuário
    // A lógica de segurança (dono ou admin) está dentro do Service.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        logger.info("Recebida requisição para deletar usuário ID: {}", id);

        usuarioService.deletar(id);

        // Retorna 204 No Content (padrão REST para deleção bem-sucedida)
        return ResponseEntity.noContent().build();
    }
}
