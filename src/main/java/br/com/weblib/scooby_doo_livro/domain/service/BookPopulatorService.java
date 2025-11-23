package br.com.weblib.scooby_doo_livro.domain.service;
import br.com.weblib.scooby_doo_livro.Repository.LivroRepository;
import br.com.weblib.scooby_doo_livro.domain.model.Livro.Livro;
import br.com.weblib.scooby_doo_livro.domain.model.enums.EnumCategoria;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookPopulatorService {

    private final LivroRepository livroRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Mapeamento de categorias da Google Books API para o Enum local
    private static final Map<String, EnumCategoria> CATEGORY_MAPPING = Map.ofEntries(
            Map.entry("Fiction", EnumCategoria.FICCAO),
            Map.entry("Fantasy", EnumCategoria.FANTASIA),
            Map.entry("Romance", EnumCategoria.ROMANCE),
            Map.entry("Horror", EnumCategoria.TERROR),
            Map.entry("Thriller", EnumCategoria.SUSPENSE),
            Map.entry("Suspense", EnumCategoria.SUSPENSE),
            Map.entry("Mystery", EnumCategoria.MISTERIO),
            Map.entry("Science Fiction", EnumCategoria.FICCAO_CIENTIFICA),
            Map.entry("Dystopian", EnumCategoria.DISTOPIA),
            Map.entry("Biography", EnumCategoria.BIOGRAFIA),
            Map.entry("Self-Help", EnumCategoria.AUTOAJUDA),
            Map.entry("History", EnumCategoria.HISTORIA),
            Map.entry("Philosophy", EnumCategoria.FILOSOFIA),
            Map.entry("Religion", EnumCategoria.RELIGIAO),
            Map.entry("Education", EnumCategoria.EDUCACAO),
            Map.entry("Poetry", EnumCategoria.POESIA),
            Map.entry("Drama", EnumCategoria.DRAMA),
            Map.entry("Humor", EnumCategoria.HUMOR),
            Map.entry("Business", EnumCategoria.NEGOCIOS),
            Map.entry("Technology", EnumCategoria.TECNOLOGIA),
            Map.entry("Computers", EnumCategoria.PROGRAMACAO),
            Map.entry("Juvenile Fiction", EnumCategoria.INFANTIL),
            Map.entry("Young Adult", EnumCategoria.JUVENIL),
            Map.entry("Art", EnumCategoria.ARTE),
            Map.entry("Science", EnumCategoria.CIENCIAS),
            Map.entry("Health", EnumCategoria.SAUDE),
            Map.entry("Sports", EnumCategoria.ESPORTES)
    );

    // Termos de busca focados em livros famosos e bestsellers
    private static final List<String> SEARCH_QUERIES = Arrays.asList(
            // Clássicos mundialmente famosos
            "Harry Potter J.K. Rowling",
            "Lord of the Rings Tolkien",
            "1984 George Orwell",
            "Pride and Prejudice Jane Austen",
            "The Great Gatsby",
            "To Kill a Mockingbird",
            "The Catcher in the Rye",

            // Bestsellers contemporâneos
            "Dan Brown",
            "Stephen King bestseller",
            "John Green",
            "Suzanne Collins Hunger Games",
            "Veronica Roth Divergent",

            // Clássicos brasileiros
            "Machado de Assis",
            "Paulo Coelho",
            "Clarice Lispector",

            // Fantasia popular
            "Game of Thrones George Martin",
            "Percy Jackson",
            "Twilight Stephenie Meyer",

            // Ficção científica famosa
            "Isaac Asimov",
            "Arthur C. Clarke",
            "Ray Bradbury",

            // Mistério e suspense bestsellers
            "Agatha Christie",
            "Sherlock Holmes",
            "Gone Girl Gillian Flynn"
    );

    /**
     * Popula o banco de dados com livros da Google Books API
     * @param maxBooks Número máximo de livros a serem importados
     */
    public void popularBancoDeDados(int maxBooks) {
        log.info("Iniciando população do banco de dados com {} livros", maxBooks);

        int livrosImportados = 0;
        int tentativas = 0;
        int maxTentativas = SEARCH_QUERIES.size() * 5; // Limita tentativas

        for (String query : SEARCH_QUERIES) {
            if (livrosImportados >= maxBooks) break;

            try {
                List<Livro> livros = buscarLivrosPorQuery(query, 40);

                for (Livro livro : livros) {
                    if (livrosImportados >= maxBooks) break;

                    tentativas++;
                    if (tentativas > maxTentativas) break;

                    // Verifica se o livro já existe no banco
                    if (!livroRepository.existsByTituloAndAutor(livro.getTitulo(), livro.getAutor())) {
                        livroRepository.save(livro);
                        livrosImportados++;
                        log.info("Livro importado ({}/{}): {} - {}",
                                livrosImportados, maxBooks, livro.getTitulo(), livro.getAutor());
                    }
                }

                // Delay para não sobrecarregar a API
                Thread.sleep(500);

            } catch (Exception e) {
                log.error("Erro ao buscar livros para query '{}': {}", query, e.getMessage());
            }
        }

        log.info("População concluída! Total de livros importados: {}", livrosImportados);
    }

    /**
     * Busca livros na Google Books API por query
     */
    private List<Livro> buscarLivrosPorQuery(String query, int maxResults) {
        List<Livro> livros = new ArrayList<>();

        try {
            String url = String.format(
                    "https://www.googleapis.com/books/v1/volumes?q=%s&maxResults=%d&langRestrict=pt&orderBy=relevance",
                    query.replace(" ", "+"), maxResults
            );

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    try {
                        Livro livro = mapearLivro(item);
                        if (livro != null && isLivroValido(livro)) {
                            livros.add(livro);
                        }
                    } catch (Exception e) {
                        log.warn("Erro ao mapear livro: {}", e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Erro ao buscar livros na API: {}", e.getMessage());
        }

        return livros;
    }

    /**
     * Mapeia os dados da API para o modelo Livro
     */
    private Livro mapearLivro(JsonNode item) {
        JsonNode volumeInfo = item.get("volumeInfo");
        if (volumeInfo == null) return null;

        Livro livro = new Livro();

        // Título (limitado a 500 caracteres)
        JsonNode titulo = volumeInfo.get("title");
        if (titulo != null) {
            String tituloText = titulo.asText();
            livro.setTitulo(truncate(tituloText, 500));
        }

        // Autor(es) (limitado a 500 caracteres)
        JsonNode autores = volumeInfo.get("authors");
        if (autores != null && autores.isArray() && autores.size() > 0) {
            List<String> listaAutores = new ArrayList<>();
            autores.forEach(autor -> listaAutores.add(autor.asText()));
            String autoresText = String.join(", ", listaAutores);
            livro.setAutor(truncate(autoresText, 500));
        }

        // Capa (thumbnail) - limitado a 1000 caracteres
        JsonNode imageLinks = volumeInfo.get("imageLinks");
        if (imageLinks != null) {
            JsonNode thumbnail = imageLinks.get("thumbnail");
            if (thumbnail != null) {
                // Remove http e usa https
                String capaUrl = thumbnail.asText().replace("http://", "https://");
                livro.setCapa(truncate(capaUrl, 1000));
            }
        }

        // Ano de publicação
        JsonNode publishedDate = volumeInfo.get("publishedDate");
        if (publishedDate != null) {
            try {
                String data = publishedDate.asText();
                // Extrai apenas o ano (formato pode ser yyyy, yyyy-MM, ou yyyy-MM-dd)
                int ano = Integer.parseInt(data.substring(0, 4));
                livro.setAno(ano);
            } catch (Exception e) {
                log.warn("Erro ao parsear ano: {}", e.getMessage());
            }
        }

        // Sinopse (description) - limitado a 2000 caracteres
        JsonNode description = volumeInfo.get("description");
        if (description != null) {
            String sinopse = description.asText();
            livro.setSinopse(truncate(sinopse, 2000));
        }

        // Categorias
        JsonNode categories = volumeInfo.get("categories");
        if (categories != null && categories.isArray()) {
            List<EnumCategoria> categoriasLivro = new ArrayList<>();

            for (JsonNode category : categories) {
                String categoryText = category.asText();

                // Tenta mapear para o enum local
                for (Map.Entry<String, EnumCategoria> entry : CATEGORY_MAPPING.entrySet()) {
                    if (categoryText.toLowerCase().contains(entry.getKey().toLowerCase())) {
                        if (!categoriasLivro.contains(entry.getValue())) {
                            categoriasLivro.add(entry.getValue());
                        }
                    }
                }
            }

            // Se não encontrou categoria, adiciona uma padrão
            if (categoriasLivro.isEmpty()) {
                categoriasLivro.add(EnumCategoria.FICCAO);
            }

            livro.setCategorias(categoriasLivro);
        } else {
            // Categoria padrão se não houver
            livro.setCategorias(List.of(EnumCategoria.FICCAO));
        }

        return livro;
    }

    /**
     * Valida se o livro tem os dados mínimos necessários
     */
    private boolean isLivroValido(Livro livro) {
        return livro.getTitulo() != null && !livro.getTitulo().isEmpty() &&
                livro.getAutor() != null && !livro.getAutor().isEmpty() &&
                livro.getCapa() != null && !livro.getCapa().isEmpty();
    }

    /**
     * Trunca uma string para o tamanho máximo, adicionando "..." se necessário
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return null;
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}