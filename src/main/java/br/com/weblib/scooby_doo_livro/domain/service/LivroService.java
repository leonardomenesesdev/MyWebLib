package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.AvaliacaoRepository;
import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.util.List;

@Service
public class LivroService {
    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public List<Livro> listarLivros(){
        return livroRepository.findAll();
    }

    public Livro getLivroById(Long id){
        return livroRepository.findById(id).orElseThrow();
    }

    public List<Livro> buscarPorTitulo(String titulo){
        return livroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Livro> getByAutor(String autor){
        return livroRepository.findByAutorContainingIgnoreCase(autor);
    }

    public List<Livro> getByCategoria(EnumCategoria categoria){
        return livroRepository.findByCategoriasContaining(categoria);
    }

    public Livro cadastrar(Livro livro, Usuario usuario) throws AuthenticationException {
            validarLivro(livro);
            if(livroRepository.existsByTituloAndAutor(livro.getTitulo(), livro.getAutor())){
                throw new RuntimeException("Livro já cadastrado: " + livro.getTitulo());
            }
            return livroRepository.save(livro);
    }


    private void validarLivro(Livro livro) {
        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }
        if (livro.getAutor() == null || livro.getAutor().trim().isEmpty()) {
            throw new IllegalArgumentException("Autor é obrigatório");
        }
        if (livro.getCategorias() == null || livro.getCategorias().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos uma categoria é obrigatória");
        }
        if (livro.getAno() != null && (livro.getAno() < 0 || livro.getAno() > 2025)) {
            throw new IllegalArgumentException("Ano de publicação inválido");
        }
    }

    public Livro atualizar(Long id, Livro livroAtualizado){
        Livro livroExistente = getLivroById(id);
        validarLivro(livroAtualizado);
        return livroRepository.save(livroAtualizado);
    }

    public void delete(Long id){
        try {
            livroRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.getMessage();
        }

    }
}

