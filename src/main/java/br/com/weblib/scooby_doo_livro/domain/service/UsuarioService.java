    package br.com.weblib.scooby_doo_livro.domain.service;

    import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
    import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
    import org.springframework.http.HttpStatus;
    import org.springframework.stereotype.Service;
    import org.springframework.web.server.ResponseStatusException;

    import java.util.List;

    @Service
    public class UsuarioService {

        private final UsuarioRepository usuarioRepository;

        // injeção de dependência via construtor
        public UsuarioService(UsuarioRepository usuarioRepository) {
            this.usuarioRepository = usuarioRepository;
        }

        // LISTAR TODOS
        public List<Usuario> listarTodos() {
            return usuarioRepository.findAll();
        }

        // BUSCAR POR ID
        public Usuario buscarPorId(Long id) {
            return usuarioRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")
                    );
        }


        // CRIAR
        public Usuario criar(Usuario usuario) {
            // se quiser validar e-mail único:
            // if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            //     throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
            // }
            return usuarioRepository.save(usuario);
        }

        // ATUALIZAR
        public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
            Usuario usuario = buscarPorId(id); // já lança 404 se não existir

            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setHashSenha(usuarioAtualizado.getHashSenha());
            // aqui você atualiza os outros campos que existirão na entidade

            return usuarioRepository.save(usuario);
        }

        // DELETAR
        public void deletar(Long id) {
            Usuario usuario = buscarPorId(id); // garante que existe
            usuarioRepository.delete(usuario);
        }
    }
