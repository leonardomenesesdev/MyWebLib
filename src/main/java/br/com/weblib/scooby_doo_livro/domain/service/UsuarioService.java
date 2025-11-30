package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.repository.AvaliacaoRepository;
import br.com.weblib.scooby_doo_livro.repository.LivroFavoritadoRepository;
import br.com.weblib.scooby_doo_livro.repository.StatusLeituraRepository;
import br.com.weblib.scooby_doo_livro.repository.UsuarioRepository;
import br.com.weblib.scooby_doo_livro.domain.dtos.request.UsuarioUpdateDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.EstatisticasDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.UserDetailsDTO;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.UserProfileResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.enums.UserRole;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RegraDeNegocioException; // ✅ Nova exceção
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // ✅ Lombok resolve o construtor gigante
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StatusLeituraRepository statusLeituraRepository;
    private final LivroFavoritadoRepository livroFavoritadoRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    @Transactional(readOnly = true)
    public List<UserDetailsDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UserDetailsDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDetailsDTO buscarPorId(Long id) {
        // Uso direto do Repository aqui é aceitável, mas prefira buscarEntidade se quiser padronizar erro
        Usuario usuario = buscarEntidadePorId(id);
        return new UserDetailsDTO(usuario);
    }

    // ✅ ASSINATURA CORRIGIDA: Recebe UsuarioUpdateDTO
    @Transactional
    public UserDetailsDTO atualizar(Long id, UsuarioUpdateDTO dados) {

        Usuario usuarioAlvo = buscarEntidadePorId(id);
        Usuario usuarioLogado = getUsuarioAutenticado();

        // 1. Segurança: Garante que só o dono altera o perfil
        if (!usuarioLogado.getId().equals(usuarioAlvo.getId())) {
            throw new RegraDeNegocioException("Você não tem permissão para alterar este perfil.");
        }

        // 2. Atualiza Nome
        if (dados.nome() != null && !dados.nome().isBlank()) {
            usuarioAlvo.setNome(dados.nome());
        }

        // 3. Atualiza Email (com verificação de conflito)
        if (dados.email() != null && !dados.email().isBlank()
                && !dados.email().equals(usuarioAlvo.getEmail())) {

            if (usuarioRepository.existsByEmail(dados.email())) {
                throw new RegraDeNegocioException("E-mail já está em uso.");
            }
            usuarioAlvo.setEmail(dados.email());
        }

        // REMOVIDO: Lógica de atualização de senha.
        // Se precisar trocar senha no futuro, crie um endpoint específico:
        // PATCH /api/usuarios/{id}/senha

        Usuario usuarioSalvo = usuarioRepository.saveAndFlush(usuarioAlvo);

        // Retorna os dados atualizados formatados para leitura
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
        Usuario usuario = buscarEntidadePorId(id);

        long lendo = statusLeituraRepository.countByUsuarioIdAndStatusLeitura(id, EnumStatusLeitura.LENDO);
        long lido = statusLeituraRepository.countByUsuarioIdAndStatusLeitura(id, EnumStatusLeitura.LIDO);
        long queroLer = statusLeituraRepository.countByUsuarioIdAndStatusLeitura(id, EnumStatusLeitura.QUERO_LER);
        long favoritos = livroFavoritadoRepository.countByUsuarioId(id);


        long avaliacoes = 0;

        EstatisticasDTO stats = new EstatisticasDTO(queroLer, lendo, lido, favoritos, avaliacoes);

        return new UserProfileResponseDTO(usuario, stats);
    }

    // Método Interno de Busca Padronizada
    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    public List<UserDetailsDTO> buscarUsuarioPorNome(String nome) {
        List<UserDetailsDTO> usuariosFiltrados = usuarioRepository.findByNomeContainingIgnoreCase(nome);

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