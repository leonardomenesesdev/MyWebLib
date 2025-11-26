    package br.com.weblib.scooby_doo_livro.domain.service;

    import br.com.weblib.scooby_doo_livro.Repository.LivroFavoritadoRepository;
    import br.com.weblib.scooby_doo_livro.Repository.StatusLeituraRepository;
    import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
    import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.EstatisticasDTO;
    import br.com.weblib.scooby_doo_livro.domain.model.Usuario.UserDetailsDTO;
    import br.com.weblib.scooby_doo_livro.domain.model.Usuario.UserProfileResponseDTO;
    import br.com.weblib.scooby_doo_livro.domain.model.Usuario.UserRole;
    import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
    import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
    import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
    import org.springframework.http.HttpStatus;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Service;
    import org.springframework.web.server.ResponseStatusException;
    import org.springframework.security.core.Authentication;

    import java.util.List;

    @Service
    public class UsuarioService {

        private final UsuarioRepository usuarioRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private final StatusLeituraRepository statusLeituraRepository;
        private final LivroFavoritadoRepository livroFavoritadoRepository;
        public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder,  StatusLeituraRepository statusLeituraRepository, LivroFavoritadoRepository livroFavoritadoRepository) {
            this.usuarioRepository = usuarioRepository;
            this.passwordEncoder = passwordEncoder;
            this.statusLeituraRepository = statusLeituraRepository;
            this.livroFavoritadoRepository = livroFavoritadoRepository;
        }

        // LISTAR TODOS
        public List<UserDetailsDTO> listarTodos() {
            return usuarioRepository.findAll()
                    .stream()
                    .map(UserDetailsDTO::new) // Chama o construtor do DTO para cada usuário
                    .toList();
        }

        // BUSCAR POR ID
        public UserDetailsDTO buscarPorId(Long id) {
            return usuarioRepository.findUserDetailsById(id);
        }


        public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
            Usuario usuarioAlvo = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

            Usuario usuarioLogado = getUsuarioAutenticado();
            //verifica se o usuário que está tentando editar é o mesmo usuário alvo
            if (!usuarioLogado.getId().equals(usuarioAlvo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar este perfil.");
            }

            if (usuarioAtualizado.getNome() != null) {
                usuarioAlvo.setNome(usuarioAtualizado.getNome());
            }

            // Atualiza Email (com verificação de unicidade)
            if (usuarioAtualizado.getEmail() != null &&
                    !usuarioAtualizado.getEmail().equals(usuarioAlvo.getEmail())) {

                if (usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já em uso.");
                }
                usuarioAlvo.setEmail(usuarioAtualizado.getEmail());
            }

            // Atualiza Senha
            if (usuarioAtualizado.getHashSenha() != null && !usuarioAtualizado.getHashSenha().isEmpty()) {
                // Evita encriptar duas vezes se o front mandar errado, mas idealmente a senha vem limpa
                String novaSenha = passwordEncoder.encode(usuarioAtualizado.getHashSenha());
                usuarioAlvo.setHashSenha(novaSenha);
            }

            return usuarioRepository.save(usuarioAlvo);
        }

        // DELETAR (Próprio usuário OU Admin)
        public void deletar(Long id) {
            Usuario usuarioAlvo = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

            Usuario usuarioLogado = getUsuarioAutenticado();

            // Verificações booleanas para clareza
            boolean isDonoDaConta = usuarioLogado.getId().equals(usuarioAlvo.getId());
            boolean isAdmin = usuarioLogado.getRole() == UserRole.ADMIN;

            // 4. REGRA DE SEGURANÇA: Se não for dono E não for admin, bloqueia.
            if (!isDonoDaConta && !isAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este usuário.");
            }

            usuarioRepository.delete(usuarioAlvo);
        }

        // METODO AUXILIAR PRIVADO (Pega o usuário do contexto de segurança)
        private Usuario getUsuarioAutenticado() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
            }
            // Como seu Model implementa UserDetails, o Principal É o Usuario.
            // O Cast (Usuario) funciona se seu Filter de autenticação estiver configurado corretamente.
            try {
                return (Usuario) authentication.getPrincipal();
            } catch (ClassCastException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao identificar usuário logado");
            }
        }
        public UserProfileResponseDTO buscarPerfilCompleto(Long id) {
            // 1. Busca Usuário (Entidade Pura)
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

            // 2. Busca Contagens (Cálculo separado)
            long lendo = statusLeituraRepository.countByIdUsuarioAndStatusLeitura(id, EnumStatusLeitura.LENDO);
            long lido = statusLeituraRepository.countByIdUsuarioAndStatusLeitura(id, EnumStatusLeitura.LIDO);
            long queroLer = statusLeituraRepository.countByIdUsuarioAndStatusLeitura(id, EnumStatusLeitura.QUERO_LER);

            // TODO: Implementar lógica real para estes dois futuramente
            long favoritos = livroFavoritadoRepository.countByUsuarioId(id);
            long avaliacoes = 0;

            // 3. Monta o objeto de estatísticas
            EstatisticasDTO stats = new EstatisticasDTO(queroLer, lendo, lido, favoritos, avaliacoes);

            // 4. Retorna o DTO Composto (Usuário + Stats)
            return new UserProfileResponseDTO(usuario, stats);
        }

        public Usuario buscarEntidadePorId(Long id) {
            return usuarioRepository.findById(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        }
    }
