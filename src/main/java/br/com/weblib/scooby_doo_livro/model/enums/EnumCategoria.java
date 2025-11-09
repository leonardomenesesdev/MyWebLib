package br.com.weblib.scooby_doo_livro.model.enums;

public enum EnumCategoria {
    FICCAO("Ficção"),
    FANTASIA("Fantasia"),
    ROMANCE("Romance"),
    TERROR("Terror"),
    SUSPENSE("Suspense"),
    MISTERIO("Mistério"),
    FICCAO_CIENTIFICA("Ficção Científica"),
    DISTOPIA("Distopia"),
    BIOGRAFIA("Biografia"),
    AUTOAJUDA("Autoajuda"),
    HISTORIA("História"),
    FILOSOFIA("Filosofia"),
    RELIGIAO("Religião"),
    EDUCACAO("Educação"),
    POESIA("Poesia"),
    DRAMA("Drama"),
    HUMOR("Humor"),
    NEGOCIOS("Negócios"),
    TECNOLOGIA("Tecnologia"),
    PROGRAMACAO("Programação"),
    INFANTIL("Infantil"),
    JUVENIL("Juvenil"),
    ARTE("Arte"),
    CIENCIAS("Ciências"),
    SAUDE("Saúde"),
    ESPORTES("Esportes");

    private final String descricao;

    EnumCategoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
