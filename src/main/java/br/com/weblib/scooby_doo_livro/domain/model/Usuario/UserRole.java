package br.com.weblib.scooby_doo_livro.domain.model.Usuario;

public enum UserRole {
    ADMIN("admin"),
    USER("usuario");

    private String role;
    private UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
