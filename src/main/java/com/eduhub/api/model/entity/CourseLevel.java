package com.eduhub.api.model.entity;

/**
 * Enum CourseCategory - Define os níveis dos cursos
 */
public enum CourseLevel {
    BASICO("Basico", "Nível Basico"),
    INTERMEDIARIO("Intermediario", "Nível Intermediário"),
    AVANCADO("Avancado", "Nível Avançado");

    private final String displayName;
    private final String description;

    CourseLevel(String displayName, String description) {
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
