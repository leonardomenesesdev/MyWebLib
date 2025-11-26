package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.Repository.StatusLeituraRepository;
import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository; // Novo Import
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroDTO;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.AtualizarStatusDTO;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.StatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario; // Novo Import
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.exceptions.RecursoNaoEncontradoException; // Assumindo que você tem essa classe
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusLeituraService {

    private final StatusLeituraRepository statusLeituraRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository; // Injeção necessária agora

    public void atualizarStatus(Long idUsuario, AtualizarStatusDTO dto) {
        // 1. Busca o status existente (usando os IDs para navegação)
        var statusExistente = statusLeituraRepository.findByUsuarioIdAndLivroId(idUsuario, dto.idLivro());

        if (statusExistente.isPresent()) {
            // Se já existe, apenas atualiza o enum (não precisa buscar Usuario/Livro no banco)
            StatusLeitura status = statusExistente.get();
            status.setStatusLeitura(dto.status());
            statusLeituraRepository.save(status);
        } else {
            // 2. Se é novo, precisamos buscar as Entidades para instanciar o objeto
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

            Livro livro = livroRepository.findById(dto.idLivro())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Livro não encontrado"));

            StatusLeitura novoStatus = new StatusLeitura(usuario, livro, dto.status());
            statusLeituraRepository.save(novoStatus);
        }

        System.out.println("Alteração de status executada para: " + dto.status());
    }

    public List<LivroDTO> listarLivrosPorStatus(Long idUsuario, EnumStatusLeitura status) {
        List<Livro> livros = statusLeituraRepository.findLivrosPorStatusDoUsuario(idUsuario, status);

        return livros.stream()
                .map(LivroDTO::new)
                .toList();
    }

    public String getStatusAtual(long idUsuario, Long idLivro) {
        return statusLeituraRepository.findByUsuarioIdAndLivroId(idUsuario, idLivro)
                .map(s -> s.getStatusLeitura().name())
                .orElse(null);
    }

    public void validarPermissaoParaFavoritar(Long idUsuario, Long idLivro) {
        StatusLeitura status = statusLeituraRepository.findByUsuarioIdAndLivroId(idUsuario, idLivro)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "É necessário definir o status LENDO ou LIDO antes de favoritar."));

        if (status.getStatusLeitura() == EnumStatusLeitura.QUERO_LER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é permitido favoritar livros da lista 'Quero Ler'.");
        }
    }
}