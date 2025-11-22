//package br.com.weblib.scooby_doo_livro.domain.service;
//
//import br.com.weblib.scooby_doo_livro.Repository.AvaliacaoRepository;
//import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
//import br.com.weblib.scooby_doo_livro.Repository.UsuarioRepository;
//import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao;
//import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
//import br.com.weblib.scooby_doo_livro.domain.model.Usuario.Usuario;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class AvaliacaoService {
//
//    private AvaliacaoRepository avaliacaoRepository;
//    private UsuarioRepository usuarioRepository;
//    private LivroRepository livroRepository;
//
//    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository) {
//        this.avaliacaoRepository = avaliacaoRepository;
//    }
//
//    public Avaliacao avaliarLivro(Long livroId, Long usuarioId, Integer nota){
//        validarNota(nota);
//        Livro livro = livroRepository.findById(livroId).orElseThrow(() -> new RuntimeException("Livro não encontrado"));
//        Usuario usuario = usuarioRepository.findById(usuarioId)
//                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
//        Optional<Avaliacao> avaliacaoExistente =
//                avaliacaoRepository.findByLivroAndUsuario(livro, usuario);
//
//        Avaliacao avaliacao;
//        if (avaliacaoExistente.isPresent()) {
//            // Atualiza a avaliação existente
//            avaliacao = avaliacaoExistente.get();
//            avaliacao.setNota(nota);
//        } else {
//            // Cria nova avaliação
//            avaliacao = new Avaliacao();
//            avaliacao.setLivro(livro);
//            avaliacao.setUsuario(usuario);
//            avaliacao.setNota(nota);
//        }
//
//        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
//
//        // Recalcula a média geral do livro
//        recalcularMediaLivro(livro);
//
//        return avaliacaoSalva;
//    }
//
//    public void removerAvaliacao(Long idLivro, Long idUsuario) {
//        Livro livro = livroRepository.findById(idLivro)
//                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
//
//        Usuario usuario = usuarioRepository.findById(idUsuario)
//                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
//
//        Optional<Avaliacao> avaliacao =
//                avaliacaoRepository.findByLivroAndUsuario(livro, usuario);
//
//        if (avaliacao.isPresent()) {
//            avaliacaoRepository.delete(avaliacao.get());
//            recalcularMediaLivro(livro);
//        }
//    }
//
//    public Optional<Avaliacao> buscarAvaliacaoUsuario(Long idLivro, Long idUsuario) {
//        Livro livro = livroRepository.findById(idLivro)
//                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
//
//        Usuario usuario = usuarioRepository.findById(idUsuario)
//                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
//
//        return avaliacaoRepository.findByLivroAndUsuario(livro, usuario);
//    }
//
//    public List<Avaliacao> listarAvaliacoesPorLivro(Long livroId) {
//        Livro livro = livroRepository.findById(livroId)
//                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
//
//        return avaliacaoRepository.findByLivro(livro);
//    }
//
//
//    private void recalcularMediaLivro(Livro livro) {
//
//    }
//
//    private void validarNota(Integer nota) {
//
//    }
//}
