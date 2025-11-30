package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.StatusLeituraRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroResumoDTO;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.AtualizarStatusDTO;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.StatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.LivroInvalidoParaFavoritarException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Para logs profissionais
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusLeituraService {

    private final StatusLeituraRepository statusLeituraRepository;
    private final LivroService livroService;
    private final UsuarioService usuarioService;

    @Transactional
    public void atualizarStatus(Long idUsuario, AtualizarStatusDTO dto) {
        var statusExistente = statusLeituraRepository.findByUsuarioIdAndLivroId(idUsuario, dto.idLivro());

        if (statusExistente.isPresent()) {
            StatusLeitura status = statusExistente.get();
            status.setStatusLeitura(dto.status());
            statusLeituraRepository.save(status);
        } else {
            Usuario usuario = usuarioService.buscarEntidadePorId(idUsuario);
            Livro livro = livroService.buscarEntidadePorId(dto.idLivro());

            StatusLeitura novoStatus = new StatusLeitura(usuario, livro, dto.status());
            statusLeituraRepository.save(novoStatus);
        }
    }

    @Transactional(readOnly = true)
    public List<LivroResumoDTO> listarLivrosPorStatus(Long idUsuario, EnumStatusLeitura status) {
        List<Livro> livros = statusLeituraRepository.findLivrosPorStatusDoUsuario(idUsuario, status);

        return livros.stream()
                .map(LivroResumoDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public String getStatusAtual(Long idUsuario, Long idLivro) {
        return statusLeituraRepository.findByUsuarioIdAndLivroId(idUsuario, idLivro)
                .map(s -> s.getStatusLeitura().name())
                .orElse(null);
    }

    public void validarPermissaoParaFavoritar(Long idUsuario, Long idLivro) {
        StatusLeitura status = statusLeituraRepository.findByUsuarioIdAndLivroId(idUsuario, idLivro)
                .orElseThrow(() -> new LivroInvalidoParaFavoritarException("É necessário definir o status LENDO ou LIDO antes de favoritar."));

        if (status.getStatusLeitura() == EnumStatusLeitura.QUERO_LER) {
            throw new LivroInvalidoParaFavoritarException("Não é permitido favoritar livros da lista 'Quero Ler'.");
        }
    }
}