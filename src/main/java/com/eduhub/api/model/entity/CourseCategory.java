package com.eduhub.api.model.entity;

/**
 * Enum CourseCategory - Define as categorias dos cursos
 */
public enum CourseCategory {
    PROGRAMACAO("Programacao", "Logica de programacao e linguagem Java na pratica"),
    BANCO_DE_DADOS("Banco de dados", "Banco de dados de forma facil"),
    ADMINISTRACAO("Administracao", "Curso de Administracao");

    private final String displayName;
    private final String description;

    CourseCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
