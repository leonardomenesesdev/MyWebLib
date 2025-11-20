    package br.com.weblib.scooby_doo_livro.domain.service;

    import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
    import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.web.server.ResponseStatusException;

    import java.util.List;

    @Service
    public class UsuarioService {

        private final UsuarioRepository usuarioRepository;
        private final BCryptPasswordEncoder passwordEncoder;

        public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
            this.usuarioRepository = usuarioRepository;
            this.passwordEncoder = passwordEncoder;
        }

        // LISTAR TODOS
        public List<Usuario> listarTodos() {
            return usuarioRepository.findAll();
        }

        // BUSCAR POR ID
        public Usuario buscarPorId(Long id) {
            return usuarioRepository.findById(id)
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado")
                    );
        }


        // CRIAR
        public Usuario criar(Usuario usuario) {
            if (usuarioRepository.existsByEmail(usuario.getEmail())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "E-mail já cadastrado"
                );
            }

            boolean isAdmin = verificarEmailAdmin(usuario.getEmail());
            usuario.setAdmin(isAdmin);
            String senhaPlana = usuario.getHashSenha(); // Na verdade está em texto plano aqui
            String senhaCriptografada = criptografarSenha(senhaPlana);
            usuario.setHashSenha(senhaCriptografada);

            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            return usuarioSalvo;
        }



        // ATUALIZAR
        public Usuario atualizar(Long id, Usuario usuarioAtualizado) {

            Usuario usuario = buscarPorId(id);

            // Atualizar campos
            if (usuarioAtualizado.getNome() != null) {
                usuario.setNome(usuarioAtualizado.getNome());
            }

            // Verificar se email já existe (se foi alterado)
            if (usuarioAtualizado.getEmail() != null &&
                    !usuarioAtualizado.getEmail().equals(usuario.getEmail())) {

                if (usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "E-mail já cadastrado por outro usuário"
                    );
                }
                boolean novoAdmin = verificarEmailAdmin(usuarioAtualizado.getEmail());
                usuario.setAdmin(novoAdmin);
                usuario.setEmail(usuarioAtualizado.getEmail());
            }

            if (usuarioAtualizado.getHashSenha() != null &&
                    !usuarioAtualizado.getHashSenha().isEmpty()) {

                // Verificar se a senha enviada já está criptografada (começa com $2a$)
                if (!usuarioAtualizado.getHashSenha().startsWith("$2a$")) {
                    String senhaCriptografada = criptografarSenha(usuarioAtualizado.getHashSenha());
                    usuario.setHashSenha(senhaCriptografada);
                }
            }

            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            return usuarioSalvo;
        }
        // DELETAR
        public void deletar(Long id) {
            Usuario usuario = buscarPorId(id); // garante que existe
            usuarioRepository.delete(usuario);
        }

        // FUNÇÕES AUXILIARES
        private String criptografarSenha(String senhaPlana) {
            if (senhaPlana == null || senhaPlana.isEmpty()) {
                throw new IllegalArgumentException("Senha não pode ser vazia");
            }

            String hash = passwordEncoder.encode(senhaPlana);
            return hash;
        }

        private boolean verificarEmailAdmin(String email) {
            if(email == null || email.isEmpty()){
                return false;
            }
            boolean isAdmin = email.toLowerCase().endsWith("@unifor.br");
            return isAdmin;
        }
    }
