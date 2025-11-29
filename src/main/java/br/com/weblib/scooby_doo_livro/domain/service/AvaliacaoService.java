package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.AvaliacaoRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao.Avaliacao;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioService usuarioService;
    private final LivroService livroService;

    @Transactional
    public Avaliacao avaliarLivro(Long livroId, Long usuarioId, Integer nota) {
        validarNota(nota);

        Livro livro = livroService.buscarEntidadePorId(livroId);
        Usuario usuario = usuarioService.buscarEntidadePorId(usuarioId);

        Avaliacao avaliacao = avaliacaoRepository.findByLivroAndUsuario(livro, usuario)
                .orElseGet(() -> {
                    Avaliacao nova = new Avaliacao();
                    nova.setLivro(livro);
                    nova.setUsuario(usuario);
                    return nova;
                });

        avaliacao.setNota(nota);
        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

        recalcularMedia(livro);

        return avaliacaoSalva;
    }

    @Transactional
    public void removerAvaliacao(Long idLivro, Long idUsuario) {
        Avaliacao avaliacao = avaliacaoRepository.findByLivroIdAndUsuarioId(idLivro, idUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliação não encontrada para remoção."));

        avaliacaoRepository.delete(avaliacao);

        Livro livro = livroService.buscarEntidadePorId(idLivro);
        recalcularMedia(livro);
    }

    @Transactional(readOnly = true)
    public Integer obterNotaDoUsuario(Long idLivro, Long idUsuario) {
        return avaliacaoRepository.findByLivroIdAndUsuarioId(idLivro, idUsuario)
                .map(Avaliacao::getNota)
                .orElse(0);
    }

    private void recalcularMedia(Livro livro) {
        Double novaMedia = avaliacaoRepository.obterMediaPorLivro(livro);
        if (novaMedia == null) novaMedia = 0.0;
        livroService.atualizarMedia(livro.getId(), novaMedia);
    }

    private void validarNota(Integer nota) {
        if (nota < 0 || nota > 5) {
            throw new RegraDeNegocioException("A nota deve ser um valor entre 0 e 5.");
        }
    }
}