package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroDTO;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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


    public Livro cadastrar(Livro livro) {
        validarLivro(livro);

        if (livroRepository.existsByTituloAndAutor(livro.getTitulo(), livro.getAutor())) {
            throw new IllegalArgumentException("Livro já cadastrado: " + livro.getTitulo());
        }

        return livroRepository.save(livro);
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

    public Livro atualizar(Long id, Livro livroAtualizado) {
        Livro livroExistente = getLivroById(id);

        validarLivro(livroAtualizado);

        // Atualiza somente os campos permitidos
        livroExistente.setTitulo(livroAtualizado.getTitulo());
        livroExistente.setAutor(livroAtualizado.getAutor());
        livroExistente.setCapa(livroAtualizado.getCapa());
        livroExistente.setAno(livroAtualizado.getAno());
        livroExistente.setCategorias(livroAtualizado.getCategorias());
        livroExistente.setSinopse(livroAtualizado.getSinopse());

        return livroRepository.save(livroExistente);
    }

    public void delete(Long id) {
        if (!livroRepository.existsById(id)) {
            throw new IllegalArgumentException("Não existe livro com ID: " + id);
        }
        livroRepository.deleteById(id);
    }

    public Livro buscarEntidadePorId(Long id) {
        return livroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado"));
    }

}

