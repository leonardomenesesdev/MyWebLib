package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.LivroFavoritadoRepository;
import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.Repository.StatusLeituraRepository;
import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroResumoDTO;
import br.com.weblib.scooby_doo_livro.domain.model.LivroFavoritado.LivroFavoritado;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.StatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroFavoritadoService {
    private final LivroFavoritadoRepository favoritoRepository;

    // INJEÇÃO DE SERVIÇOS (Não Repositórios)
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
        // 1. Delega a validação para o domínio de Status
        // Se a regra mudar, mudamos lá, e não aqui.
        statusLeituraService.validarPermissaoParaFavoritar(idUsuario, idLivro);

        // 2. Busca entidades através dos seus serviços guardiões
        // Nota: Certifique-se que seus services tem métodos que retornam a Entidade (não DTO)
        // para uso interno (pacote service), ou use um método getReferenceById se quiser performance.
        Livro livro = livroService.buscarEntidadePorId(idLivro);
        Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);

        // 3. Salva
        LivroFavoritado novoFavorito = new LivroFavoritado();
        novoFavorito.setLivro(livro);
        novoFavorito.setUsuario(usuario);
        novoFavorito.setDataAdicao(LocalDateTime.now());

        favoritoRepository.save(novoFavorito);
    }

    public boolean isFavorito(Long idUsuario, Long idLivro) {
        return favoritoRepository.existsByUsuarioIdAndLivroId(idUsuario, idLivro);
    }

    public List<LivroResumoDTO> listarFavoritosDoUsuario(Long idUsuario) {
        // 1. Busca as entidades 'LivroFavoritado' do banco
        List<LivroFavoritado> favoritos = favoritoRepository.findAllByUsuarioId(idUsuario);

        // 2. Mapeia: De (LivroFavoritado) -> Pega o (Livro) -> Transforma em (DTO)
        return favoritos.stream()
                .map(favorito -> new LivroResumoDTO(favorito.getLivro()))
                .toList(); // Retorna lista imutável (Java 16+)
    }
}
