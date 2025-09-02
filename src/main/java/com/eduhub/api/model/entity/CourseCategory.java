package com.eduhub.api.model.entity;

/**
 * Enum CourseCategory - Categorias de cursos
 */
public enum CourseCategory {
    PROGRAMMING("Programação", "Cursos de desenvolvimento de software"),
    DESIGN("Design", "Cursos de design gráfico, UI/UX"),
    BUSINESS("Negócios", "Cursos de empreendedorismo e gestão"),
    MARKETING("Marketing", "Cursos de marketing digital e vendas"),
    DATA_SCIENCE("Ciência de Dados", "Cursos de análise de dados e IA"),
    LANGUAGES("Idiomas", "Cursos de línguas estrangeiras"),
    MUSIC("Música", "Cursos de instrumentos e teoria musical"),
    PHOTOGRAPHY("Fotografia", "Cursos de fotografia e edição"),
    FITNESS("Fitness", "Cursos de exercícios e saúde"),
    COOKING("Culinária", "Cursos de gastronomia e culinária");

    private final String displayName;
    private final String description;

    CourseCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    public static CourseCategory fromString(String category) {
        if (category == null) return null;
        for (CourseCategory cat : CourseCategory.values()) {
            if (cat.name().equalsIgnoreCase(category)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Categoria inválida: " + category);
    }

    @Override
    public String toString() { return displayName; }
}