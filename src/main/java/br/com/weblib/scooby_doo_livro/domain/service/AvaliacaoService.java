package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.AvaliacaoRepository;
import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao.Avaliacao;
import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        Avaliacao avaliacao = avaliacaoExistente.orElse(new Avaliacao());
        if (avaliacaoExistente.isEmpty()) {
            avaliacao.setLivro(livro);
            avaliacao.setUsuario(usuario);
        }
        avaliacao.setNota(nota);

        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);

        // Recalcula a média geral do livro
        atualizarMediaDoLivro(livro);

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
            atualizarMediaDoLivro(livro);
        } else {
            throw new RecursoNaoEncontradoException("Avaliação não " +
                    "encontrada");        }
    }


    private void atualizarMediaDoLivro(Livro livro) {
        // Passo 1: O Banco calcula a média (retorna apenas um Double)
        Double novaMedia = avaliacaoRepository.obterMediaPorLivro(livro);

        // Passo 2: Atualizamos o Livro com a nova média
        // Opção A: Atualizar o objeto em memória e salvar (padrão JPA)
        // livro.setAvaliacao_media(novaMedia);
        // livroRepository.save(livro);

        // Opção B (A que você pediu): Query direta de Update no repositório
        livroRepository.atualizarMediaDoLivro(livro.getId(), novaMedia);

        // Atualiza o objeto em memória caso ele seja retornado na resposta do controller
        livro.setAvaliacao_media(novaMedia);
    }

    private void validarNota(Integer nota) {
        if (nota < 0 || nota > 5) {
            throw new IllegalArgumentException("Nota deve ser entre 0 e 5");
        }
    }
}
