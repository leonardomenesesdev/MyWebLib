package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.ComentarioRepository;
import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.Comentario;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.ComentarioRequestDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.ComentarioResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.UserRole;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final LivroService livroService;


    @Transactional
    public ComentarioResponseDTO adicionar(ComentarioRequestDTO dto) {
        Usuario usuarioLogado = getUsuarioAutenticado();

        Livro livro = livroService.buscarEntidadePorId(dto.idLivro());

        Comentario comentario = new Comentario();
        comentario.setUsuario(usuarioLogado);
        comentario.setLivro(livro);
        comentario.setConteudo(dto.conteudo());
        comentario.setData(new Date());

        if (dto.idComentarioPai() != null) {
            Comentario pai = comentarioRepository.findById(dto.idComentarioPai())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Comentário pai não encontrado."));

            if (!pai.getLivro().getId().equals(livro.getId())) {
                throw new RegraDeNegocioException("O comentário respondido pertence a outro livro.");
            }

            comentario.setPai(pai);
        }

        Comentario salvo = comentarioRepository.save(comentario);
        return new ComentarioResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public Page<ComentarioResponseDTO> listarPorLivro(Long idLivro, Pageable pageable) {
        if (!livroService.existePorId(idLivro)) {
            throw new RecursoNaoEncontradoException("Livro não encontrado.");
        }
        return comentarioRepository.findByLivroId(idLivro, pageable)
                .map(ComentarioResponseDTO::new);
    }

    @Transactional
    public void excluir(Long idComentario) {
        Comentario comentario = comentarioRepository.findById(idComentario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Comentário não encontrado."));

        Usuario usuarioLogado = getUsuarioAutenticado();

        boolean isDono = comentario.getUsuario().getId().equals(usuarioLogado.getId());
        boolean isAdmin = usuarioLogado.getRole() == UserRole.ADMIN;

        if (!isDono && !isAdmin) {
            throw new RegraDeNegocioException("Você não tem permissão para excluir este comentário.");
        }

        comentarioRepository.delete(comentario);
    }

    private Usuario getUsuarioAutenticado() {
        try {
            return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new RegraDeNegocioException("Usuário não autenticado ou contexto inválido.");
        }
    }
}
