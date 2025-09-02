package com.eduhub.api.model.entity;

/**
 * Enum LessonType - Tipos de aula
 */
public enum LessonType {
    VIDEO("Vídeo", "Aula em formato de vídeo"),
    TEXT("Texto", "Aula em formato textual"),
    QUIZ("Quiz", "Questionário de avaliação"),
    ASSIGNMENT("Tarefa", "Exercício prático"),
    LIVE("Ao Vivo", "Aula ao vivo"),
    DOWNLOAD("Download", "Material para download");

    private final String displayName;
    private final String description;

    LessonType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    public static LessonType fromString(String type) {
        if (type == null) return null;
        for (LessonType lessonType : LessonType.values()) {
            if (lessonType.name().equalsIgnoreCase(type)) {
                return lessonType;
            }
        }
        throw new IllegalArgumentException("Tipo de aula inválido: " + type);
    }

    @Override
    public String toString() { return displayName; }
}