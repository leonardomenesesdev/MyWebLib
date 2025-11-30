package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.LivroFavoritadoRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroResumoDTO;
import br.com.weblib.scooby_doo_livro.domain.model.LivroFavoritado.LivroFavoritado;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroFavoritadoService {

    private final LivroFavoritadoRepository favoritoRepository;

    // Injeção de Services (Orquestração Correta)
    private final StatusLeituraService statusLeituraService;
    private final LivroService livroService;
    private final UsuarioService usuarioService;

    @Transactional
    public void alternarFavorito(Long idUsuario, Long idLivro) {
        var favoritoExistente = favoritoRepository.findByUsuarioIdAndLivroId(idUsuario, idLivro);

        if (favoritoExistente.isPresent()) {
            favoritoRepository.delete(favoritoExistente.get());
        } else {
            adicionarFavorito(idUsuario, idLivro);
        }
    }

    private void adicionarFavorito(Long idUsuario, Long idLivro) {
        statusLeituraService.validarPermissaoParaFavoritar(idUsuario, idLivro);
        Livro livro = livroService.buscarEntidadePorId(idLivro);
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);

        // 3. Persistência
        LivroFavoritado novoFavorito = new LivroFavoritado();
        novoFavorito.setLivro(livro);
        novoFavorito.setUsuario(usuario);
        novoFavorito.setDataAdicao(LocalDateTime.now());

        favoritoRepository.save(novoFavorito);
    }

    @Transactional(readOnly = true)
    public boolean isFavorito(Long idUsuario, Long idLivro) {
        return favoritoRepository.existsByUsuarioIdAndLivroId(idUsuario, idLivro);
    }

    @Transactional(readOnly = true)
    public List<LivroResumoDTO> listarFavoritosDoUsuario(Long idUsuario) {

        return favoritoRepository.findAllByUsuarioId(idUsuario)
                .stream()
                .map(favorito -> new LivroResumoDTO(favorito.getLivro()))
                .toList();
    }
}