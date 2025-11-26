package br.com.weblib.scooby_doo_livro.domain.model.Usuario;

import br.com.weblib.scooby_doo_livro.domain.model.Avaliacao.Avaliacao;
import br.com.weblib.scooby_doo_livro.domain.model.Comentario.Comentario;
import br.com.weblib.scooby_doo_livro.domain.model.LivroFavoritado.LivroFavoritado;
import br.com.weblib.scooby_doo_livro.domain.model.StatusLeitura.StatusLeitura;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String hashSenha;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    // 1. Avaliações feitas pelo usuário
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    // Evita que ao buscar usuário venha o histórico inteiro de avaliações
    @ToString.Exclude
    private List<Avaliacao> avaliacoes;

    // 2. Comentários feitos pelo usuário
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Comentario> comentarios;

    // 3. Status de Leitura (Lendo, Lido, Quero Ler)
//     Se você tiver uma entidade StatusLeitura que liga Usuario e Livro
     @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonIgnore
     @ToString.Exclude
     private List<StatusLeitura> statusLeitura;

    // 4. Livros Favoritados
     @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonIgnore
     @ToString.Exclude
     private List<LivroFavoritado> favoritos;

    public Usuario(String nome, String email, String hashSenha, UserRole role) {
        this.nome = nome;
        this.email = email;
        this.hashSenha = hashSenha;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // spring meio que trabalha com as permissões "acumulativas"
        // a role de admin só tem permissões exclusivas para admin, mas
        // um admin também tem as permissões de um usuário normal.
        // então um usuário admin, no spring, vai precisar ter tanto a
        // role admin quanto a role user para ter suas permissões corretas.

        if (this.role == UserRole.ADMIN) {

            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return this.hashSenha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
