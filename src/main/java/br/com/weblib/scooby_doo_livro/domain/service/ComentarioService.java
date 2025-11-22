package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.ComentarioRepository;
import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.Comentario;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.ComentarioRequestDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.ComentarioResponseDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.UserRole;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final LivroRepository livroRepository;

    public ComentarioResponseDTO comentar(ComentarioRequestDTO dto){
        Usuario usuario = getUsuarioAutenticado();

        Livro livro = livroRepository.findById(dto.idLivro()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setLivro(livro);
        comentario.setConteudo(dto.conteudo());
        comentario.setData(new Date());

        Comentario salvo = comentarioRepository.save(comentario);
        return new ComentarioResponseDTO(salvo);
    }

    public Page<ComentarioResponseDTO> listarPorLivro(Long idLivro, Pageable pageable) {
        if (!livroRepository.existsById(idLivro)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado");
        }
        return comentarioRepository.findByLivroId(idLivro, pageable)
                .map(ComentarioResponseDTO::new);
    }

    public void excluir(Long idComentario) {
        Comentario comentario = comentarioRepository.findById(idComentario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentário não encontrado"));

        Usuario usuarioLogado = getUsuarioAutenticado();

        boolean isDono = comentario.getUsuario().getId().equals(usuarioLogado.getId());
        boolean isAdmin = usuarioLogado.getRole() == UserRole.ADMIN;

        if (!isDono && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este comentário.");
        }

        comentarioRepository.delete(comentario);
    }

    private Usuario getUsuarioAutenticado() {
        try {
            return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
