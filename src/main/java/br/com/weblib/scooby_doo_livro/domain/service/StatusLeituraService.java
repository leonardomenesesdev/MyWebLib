package br.com.weblib.scooby_doo_livro.domain.service;

import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.Repository.StatusLeituraRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.LivroDTO;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.AtualizarStatusDTO;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.StatusLeitura;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumStatusLeitura;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusLeituraService {
    private final StatusLeituraRepository statusLeituraRepository;
    private final LivroRepository livroRepository;

    public void atualizarStatus(Long idUsuario, AtualizarStatusDTO dto){
        if(!livroRepository.existsById(dto.idLivro())){
            throw new IllegalArgumentException("Livro não encontrado com ID: "+dto.idLivro());
        }

        StatusLeitura status = statusLeituraRepository.findByIdUsuarioAndIdLivro(idUsuario, dto.idLivro())
                .orElseGet(()->new StatusLeitura(idUsuario, dto.idLivro(), dto.status()));
        status.setStatusLeitura(dto.status());
        statusLeituraRepository.save(status);
        System.out.println("alteracao executada");
    }
    public List<LivroDTO> listarLivrosPorStatus(Long idUsuario, EnumStatusLeitura status) {
        List<Livro> livros = statusLeituraRepository.findLivrosPorStatusDoUsuario(idUsuario, status);

        return livros.stream()
                .map(LivroDTO::new)
                .toList();
    }
    public String getStatusAtual(long idUsuario, Long idLivro){
        return statusLeituraRepository.findByIdUsuarioAndIdLivro(idUsuario, idLivro).map(
                s->s.getStatusLeitura().name())
                .orElse(null);
    }
}
