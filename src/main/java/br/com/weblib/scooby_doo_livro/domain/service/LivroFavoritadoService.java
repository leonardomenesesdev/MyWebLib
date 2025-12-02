package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import br.com.weblib.scooby_doo_livro.repository.LivroFavoritadoRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro;
import br.com.weblib.scooby_doo_livro.domain.dtos.response.LivroResumoDTO;
import br.com.weblib.scooby_doo_livro.domain.model.LivroFavoritado;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroFavoritadoService {

    private final LivroFavoritadoRepository favoritoRepository;

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
    public long contarFavoritos(Long userId) {
        return favoritoRepository.countByUsuarioId(userId);
    }
}