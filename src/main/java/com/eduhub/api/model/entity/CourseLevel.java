package com.eduhub.api.model.entity;

/**
 * Enum CourseLevel - Níveis de dificuldade
 */
public enum CourseLevel {
    BEGINNER("Iniciante", "Para quem está começando"),
    INTERMEDIATE("Intermediário", "Para quem já tem conhecimentos básicos"),
    ADVANCED("Avançado", "Para quem já domina o assunto"),
    EXPERT("Expert", "Para profissionais experientes");

    private final String displayName;
    private final String description;

    CourseLevel(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    public static CourseLevel fromString(String level) {
        if (level == null) return null;
        for (CourseLevel lvl : CourseLevel.values()) {
            if (lvl.name().equalsIgnoreCase(level)) {
                return lvl;
            }
        }
        throw new IllegalArgumentException("Nível inválido: " + level);
    }

    @Override
    public String toString() { return displayName; }
}