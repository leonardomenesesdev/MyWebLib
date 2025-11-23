package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.AvaliacaoRepository;
import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao;
import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private LivroRepository livroRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public Avaliacao avaliarLivro(Long livroId, Long usuarioId, Integer nota) {
        validarNota(nota);
        Livro livro =
                livroRepository.findById(livroId).orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Livro não encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Optional<Avaliacao> avaliacaoExistente =
                avaliacaoRepository.findByLivroAndUsuario(livro, usuario);

        Avaliacao avaliacao;
        if (avaliacaoExistente.isPresent()) {
            // Atualiza a avaliação existente
            avaliacao = avaliacaoExistente.get();
            avaliacao.setNota(nota);
        } else {
            // Cria nova avaliação
            avaliacao = new Avaliacao();
            avaliacao.setLivro(livro);
            avaliacao.setUsuario(usuario);
            avaliacao.setNota(nota);
        }

        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

        // Recalcula a média geral do livro
        recalcularMediaLivro(livro);

        return avaliacaoSalva;
    }

    public void removerAvaliacao(Long idLivro, Long idUsuario) {
        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Optional<Avaliacao> avaliacao =
                avaliacaoRepository.findByLivroAndUsuario(livro, usuario);

        if (avaliacao.isPresent()) {
            avaliacaoRepository.delete(avaliacao.get());
            recalcularMediaLivro(livro);
        } else {
            throw new RecursoNaoEncontradoException("Avaliação não " +
                    "encontrada");        }
    }

    public Avaliacao buscarAvaliacaoUsuario(Long idLivro, Long idUsuario) {
        Livro livro = livroRepository.findById(idLivro)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        Optional<Avaliacao> avaliacaoUsuario =
                avaliacaoRepository.findByLivroAndUsuario(livro, usuario);

        if (avaliacaoUsuario.isEmpty()) {
            throw new RecursoNaoEncontradoException("Avaliação não " +
                    "encontrada");
        }

        return avaliacaoUsuario.get();
    }

    public List<Avaliacao> listarAvaliacoesPorLivro(Long livroId) {
        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

        return avaliacaoRepository.findByLivro(livro);
    }


    public void recalcularMediaLivro(Livro livro) {
        List<Avaliacao> avaliacoes =
                this.listarAvaliacoesPorLivro(livro.getId());

        double somaAcumulada = 0.0;

        for (Avaliacao avaliacao : avaliacoes) {
            somaAcumulada += avaliacao.getNota();
        }

        double media = somaAcumulada / avaliacoes.size();

        livro.setAvaliacao_media(media);
    }

    private void validarNota(Integer nota) {
        if (nota < 0 || nota > 5) {
            throw new IllegalArgumentException("Nota deve ser entre 0 e 5");
        }
    }
}
