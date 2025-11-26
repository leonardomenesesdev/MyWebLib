package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroDTO;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroRequestDTO;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;

    public Page<LivroDTO> listarLivros(Pageable pageable) {
        // Busca a página de entidades já com categorias carregadas
        Page<Livro> livrosPage =
                livroRepository.findAllLivrosComCategorias(pageable);

        // Converte Entidade -> DTO
        return livrosPage.map(LivroDTO::new);
    }

    public Livro getLivroById(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado com ID: " + id));
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        return livroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Livro> getByAutor(String autor) {
        return livroRepository.findByAutorContainingIgnoreCase(autor);
    }

    public List<Livro> getByAutorOrTitulo(String termo){
        return livroRepository.findByAutorContainingIgnoreCaseOrTituloContainingIgnoreCase(termo, termo);
    }
    public List<Livro> buscarPorCategoria(EnumCategoria categoria) {
        return livroRepository.findByCategoriasContaining(categoria);
    }


    public Livro cadastrar(LivroRequestDTO dadosLivro) {
        validarLivro(dadosLivro);

        if (livroRepository.existsByTituloAndAutor(dadosLivro.titulo(),
                dadosLivro.autor())) {
            throw new IllegalArgumentException("Livro já cadastrado: " + dadosLivro.titulo());
        }

        Livro novoLivro = new Livro();

        novoLivro.setTitulo(dadosLivro.titulo());
        novoLivro.setAutor(dadosLivro.autor());
        novoLivro.setCapa(dadosLivro.capa());
        novoLivro.setAno(dadosLivro.ano());
        novoLivro.setCategorias(dadosLivro.categorias());
        novoLivro.setSinopse(dadosLivro.sinopse());

        return livroRepository.save(novoLivro);
    }

    private void validarLivro(Livro livro) throws IllegalArgumentException{
        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        if (livro.getAutor() == null || livro.getAutor().trim().isEmpty()) {
            throw new IllegalArgumentException("Autor é obrigatório");
        }
        if (livro.getCategorias() == null || livro.getCategorias().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos uma categoria é obrigatória");
        }

        int anoAtual = java.time.Year.now().getValue();

        if (livro.getAno() != null && (livro.getAno() < 0 || livro.getAno() > anoAtual)) {
            throw new IllegalArgumentException("Ano de publicação inválido");
        }
    }

    private void validarLivro(LivroRequestDTO livro) throws IllegalArgumentException{
        if (livro.titulo() == null || livro.titulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        if (livro.autor() == null || livro.autor().trim().isEmpty()) {
            throw new IllegalArgumentException("Autor é obrigatório");
        }
        if (livro.categorias() == null || livro.categorias().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos uma categoria é obrigatória");
        }

        int anoAtual = java.time.Year.now().getValue();

        if (livro.ano() != null && (livro.ano() < 0 || livro.ano() > anoAtual)) {
            throw new IllegalArgumentException("Ano de publicação inválido");
        }
    }

    @Transactional
    public Livro atualizar(Long id, LivroRequestDTO dadosAtualizados) {
        validarLivro(dadosAtualizados);

        Livro livroExistente = getLivroById(id);

        if (dadosAtualizados.titulo() != null) {
            livroExistente.setTitulo(dadosAtualizados.titulo());
        }

        if (dadosAtualizados.autor() != null) {
            livroExistente.setAutor(dadosAtualizados.autor());
        }

        if (dadosAtualizados.capa() != null) {
            livroExistente.setCapa(dadosAtualizados.capa());
        }

        if (dadosAtualizados.ano() != null) {
            livroExistente.setAno(dadosAtualizados.ano());
        }

        if (dadosAtualizados.categorias() != null && !dadosAtualizados.categorias().isEmpty()) {
            livroExistente.setCategorias(dadosAtualizados.categorias());
        }

        if (dadosAtualizados.sinopse() != null) {
            livroExistente.setSinopse(dadosAtualizados.sinopse());
        }

        return livroRepository.save(livroExistente);
    }

    @Transactional
    public void delete(Long id) {
        Livro livro = getLivroById(id);

        livroRepository.delete(livro);
    }

    public Livro buscarEntidadePorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado"));
    }

}

