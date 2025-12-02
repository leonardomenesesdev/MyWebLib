package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.repository.UsuarioRepository;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.EstatisticasDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.UserDetailsDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.UserProfileResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.request.UsuarioUpdateDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.enums.UserRole;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final StatusLeituraService statusLeituraService;
    private final LivroFavoritadoService livroFavoritadoService;
    private final AvaliacaoService avaliacaoService;
    public UsuarioService(UsuarioRepository usuarioRepository,
                          @Lazy StatusLeituraService statusLeituraService,
                          @Lazy LivroFavoritadoService livroFavoritadoService,
                          @Lazy AvaliacaoService avaliacaoService) {
        this.usuarioRepository = usuarioRepository;
        this.statusLeituraService = statusLeituraService;
        this.livroFavoritadoService = livroFavoritadoService;
        this.avaliacaoService = avaliacaoService;
    }

    @Transactional(readOnly = true)
    public List<UserDetailsDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UserDetailsDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDetailsDTO buscarPorId(Long id) {
        Usuario usuario = buscarEntidadePorId(id);
        return new UserDetailsDTO(usuario);
    }

    @Transactional
    public UserDetailsDTO atualizar(Long id, UsuarioUpdateDTO dados) {
        Usuario usuarioAlvo = buscarEntidadePorId(id);
        Usuario usuarioLogado = getUsuarioAutenticado();

        // Segurança
        if (!usuarioLogado.getId().equals(usuarioAlvo.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para alterar este perfil.");
        }

        // Atualiza Nome
        if (dados.nome() != null && !dados.nome().isBlank()) {
            usuarioAlvo.setNome(dados.nome());
        }

        // Atualiza Email
        if (dados.email() != null && !dados.email().isBlank()
                && !dados.email().equals(usuarioAlvo.getEmail())) {

            if (usuarioRepository.existsByEmail(dados.email())) {
                throw new RegraDeNegocioException("E-mail já está em uso.");
            }
            usuarioAlvo.setEmail(dados.email());
        }

        Usuario usuarioSalvo = usuarioRepository.saveAndFlush(usuarioAlvo);
        return new UserDetailsDTO(usuarioSalvo);
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuarioAlvo = buscarEntidadePorId(id);
        Usuario usuarioLogado = getUsuarioAutenticado();

        boolean isDonoDaConta = usuarioLogado.getId().equals(usuarioAlvo.getId());
        boolean isAdmin = usuarioLogado.getRole() == UserRole.ADMIN;

        if (!isDonoDaConta && !isAdmin) {
            throw new RegraDeNegocioException("Você não tem permissão para excluir este usuário.");
        }

        usuarioRepository.delete(usuarioAlvo);
    }

    @Transactional(readOnly = true)
    public UserProfileResponseDTO buscarPerfilCompleto(Long id) {
        // 1. Busca dados do domínio local (Usuario)
        Usuario usuario = buscarEntidadePorId(id);

        // 2. Delega a contagem para os especialistas (Outros Services)
        // Nota: Você precisará garantir que esses métodos existam nos services respectivos
        long lendo = statusLeituraService.contarPorStatus(id, EnumStatusLeitura.LENDO);
        long lido = statusLeituraService.contarPorStatus(id, EnumStatusLeitura.LIDO);
        long queroLer = statusLeituraService.contarPorStatus(id, EnumStatusLeitura.QUERO_LER);

        long favoritos = livroFavoritadoService.contarFavoritos(id);

        // Se ainda não implementou no AvaliacaoService, pode deixar 0 ou implementar lá
        long avaliacoes = avaliacaoService.contarAvaliacoesDoUsuario(id);

        // 3. Monta o DTO
        EstatisticasDTO stats = new EstatisticasDTO(queroLer, lendo, lido, favoritos, avaliacoes);

        return new UserProfileResponseDTO(usuario, stats);
    }

    // Método Interno de Busca Padronizada
    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    public List<UserDetailsDTO> buscarUsuarioPorNome(String nome) {
        List<UserDetailsDTO> usuariosFiltrados = usuarioRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(u -> new UserDetailsDTO(u.id(), u.nome(), u.email(), u.role()))
                .toList();

        if (usuariosFiltrados.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum usuário encontrado com o nome: '" + nome + "'");
        }
        return usuariosFiltrados;
    }
    private Usuario getUsuarioAutenticado() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new RegraDeNegocioException("Usuário não autenticado.");
            }
            return (Usuario) auth.getPrincipal();
        } catch (ClassCastException e) {
            throw new RegraDeNegocioException("Erro ao identificar usuário logado.");
        }
    }
}