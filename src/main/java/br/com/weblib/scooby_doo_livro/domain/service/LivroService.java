package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroRequestDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroResumoDTO;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;


    @Transactional(readOnly = true)
    public Page<LivroResumoDTO> listarLivros(Pageable pageable) {
        return livroRepository.findAllLivrosComCategorias(pageable)
                .map(LivroResumoDTO::new);
    }

    @Transactional(readOnly = true)
    public LivroDTO buscarPorId(Long id) {
        Livro livro = buscarEntidadePorId(id);
        return new LivroDTO(livro);
    }

    @Transactional(readOnly = true)
    public List<
            LivroResumoDTO> buscarPorTitulo(String titulo) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo)
                .stream()
                .map(LivroResumoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LivroResumoDTO> buscarPorAutor(String autor) {
        return livroRepository.findByAutorContainingIgnoreCase(autor)
                .stream()
                .map(LivroResumoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LivroResumoDTO> buscarPorAutorOuTitulo(String termo) {
        return livroRepository.findByAutorContainingIgnoreCaseOrTituloContainingIgnoreCase(termo, termo)
                .stream()
                .map(LivroResumoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LivroResumoDTO> buscarPorCategoria(EnumCategoria categoria) {
        return livroRepository.findByCategoriasContaining(categoria)
                .stream()
                .map(LivroResumoDTO::new)
                .toList();
    }


    @Transactional
    public LivroDTO cadastrar(LivroRequestDTO dados) {
        // Validação de Regra de Negócio (Duplicidade)
        if (livroRepository.existsByTituloAndAutor(dados.titulo(), dados.autor())) {
            throw new RegraDeNegocioException("Livro já cadastrado: " + dados.titulo());
        }

        validarAno(dados.ano());

        Livro novoLivro = new Livro();
        atualizarDadosEntidade(novoLivro, dados);

        livroRepository.save(novoLivro);

        return new LivroDTO(novoLivro);
    }

    @Transactional
    public LivroDTO atualizar(Long id, LivroRequestDTO dados) {
        Livro livro = buscarEntidadePorId(id);

        if (dados.ano() != null) {
            validarAno(dados.ano());
        }

        atualizarDadosEntidade(livro, dados);

        livroRepository.save(livro);
        return new LivroDTO(livro);
    }

    @Transactional
    public void atualizarMedia(Long idLivro, Double novaMedia) {
        Livro livro = buscarEntidadePorId(idLivro);
        livro.setAvaliacaoMedia(novaMedia);
        livroRepository.save(livro);
    }

    @Transactional
    public void delete(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Livro não encontrado para exclusão.");
        }
        livroRepository.deleteById(id);
    }


    public Livro buscarEntidadePorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado com ID: " + id));
    }

    public boolean existePorId(Long id) {
        return livroRepository.existsById(id);
    }

    // converte livro em dto
    private void atualizarDadosEntidade(Livro livro, LivroRequestDTO dados) {
        if (dados.titulo() != null) livro.setTitulo(dados.titulo());
        if (dados.autor() != null) livro.setAutor(dados.autor());
        if (dados.capa() != null) livro.setCapa(dados.capa());
        if (dados.ano() != null) livro.setAno(dados.ano());
        if (dados.sinopse() != null) livro.setSinopse(dados.sinopse());
        if (dados.categorias() != null && !dados.categorias().isEmpty()) {
            livro.setCategorias(dados.categorias());
        }
    }

    private void validarAno(Integer ano) {
        if (ano != null) {
            int anoAtual = java.time.Year.now().getValue();
            if (ano < 0 || ano > anoAtual) {
                throw new RegraDeNegocioException("Ano de publicação inválido.");
            }
        }
    }
}