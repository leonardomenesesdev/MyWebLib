    package br.com.weblib.scooby_doo_livro.domain.service;

    import br.com.weblib.scooby_doo_livro.Repository.AvaliacaoRepository;
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
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.server.ResponseStatusException;
    import org.springframework.security.core.Authentication;

    import java.util.ArrayList;
    import java.util.List;

    @Service
    public class UsuarioService {

        private final UsuarioRepository usuarioRepository;
        private final BCryptPasswordEncoder passwordEncoder;
        private final StatusLeituraRepository statusLeituraRepository;
        private final LivroFavoritadoRepository livroFavoritadoRepository;
        private final AvaliacaoRepository avaliacaoRepository;

        public UsuarioService(UsuarioRepository usuarioRepository,
                              BCryptPasswordEncoder passwordEncoder,
                              StatusLeituraRepository statusLeituraRepository,
                              LivroFavoritadoRepository livroFavoritadoRepository,
                              AvaliacaoRepository avaliacaoRepository) {
            this.usuarioRepository = usuarioRepository;
            this.passwordEncoder = passwordEncoder;
            this.statusLeituraRepository = statusLeituraRepository;
            this.livroFavoritadoRepository = livroFavoritadoRepository;
            this.avaliacaoRepository = avaliacaoRepository;
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


        public UserDetailsDTO atualizar(Long id, UserDetailsDTO dados) {

            // 1. Busca a entidade (Persistência)
            Usuario usuarioAlvo = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

            // 2. Segurança: Garante que só o dono altera o perfil
            Usuario usuarioLogado = getUsuarioAutenticado();
            if (!usuarioLogado.getId().equals(usuarioAlvo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar este perfil.");
            }

            // 3. Atualização condicional (Patch)

            // Atualiza Nome
            if (dados.nome() != null && !dados.nome().isBlank()) {
                usuarioAlvo.setNome(dados.nome());
            }

            // Atualiza Email (com verificação de conflito)
            if (dados.email() != null && !dados.email().isBlank()
                    && !dados.email().equals(usuarioAlvo.getEmail())) {

                if (usuarioRepository.existsByEmail(dados.email())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já está em uso.");
                }
                usuarioAlvo.setEmail(dados.email());
            }


            // 4. Salva e Converte para DTO de Saída
            // O saveAndFlush força a validação do banco imediatamente (útil para erros de constraint)
            Usuario usuarioSalvo = usuarioRepository.saveAndFlush(usuarioAlvo);

            // Retorna o DTO de leitura (que já existe no seu projeto)
            return new UserDetailsDTO(usuarioSalvo);
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
        @Transactional(readOnly = true) // Boa prática para métodos de leitura
        public UserProfileResponseDTO buscarPerfilCompleto(Long id) {
            // 1. Busca Usuário
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

            // 2. Busca Contagens (CORREÇÃO AQUI)
            // Mudamos de countByIdUsuario... para countByUsuarioId...
            long lendo = statusLeituraRepository.countByUsuarioIdAndStatusLeitura(id, EnumStatusLeitura.LENDO);
            long lido = statusLeituraRepository.countByUsuarioIdAndStatusLeitura(id, EnumStatusLeitura.LIDO);
            long queroLer = statusLeituraRepository.countByUsuarioIdAndStatusLeitura(id, EnumStatusLeitura.QUERO_LER);

            long favoritos = livroFavoritadoRepository.countByUsuarioId(id);

            // 3. Implementação Real das Avaliações (Bônus)
            // Supondo que você crie um método countByUsuarioId no AvaliacaoRepository
            long avaliacoes = avaliacaoRepository.countByUsuarioId(id);

            // 4. Monta o DTO
            EstatisticasDTO stats = new EstatisticasDTO(queroLer, lendo, lido, favoritos, avaliacoes);

            return new UserProfileResponseDTO(usuario, stats);
        }

        public Usuario buscarEntidadePorId(Long id) {
            return usuarioRepository.findById(id)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
        }

        public List<UserDetailsDTO> buscarUsuarioPorNome(String nome) {
            List<UserDetailsDTO> usuariosFiltrados =
                    usuarioRepository.findByNomeContainingIgnoreCase(nome);

            if (usuariosFiltrados.isEmpty()) {
                throw new RecursoNaoEncontradoException("Nenhum usuário " +
                        "foi encontrado com o nome: '" + nome + "'");
            }

            return usuariosFiltrados;
        }
    }
