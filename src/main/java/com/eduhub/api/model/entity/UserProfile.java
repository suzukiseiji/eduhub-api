package com.eduhub.api.model.entity;

/**
 * Enum UserProfile - Define os tipos de usuário do sistema
 *
 * Equivale a uma coluna ENUM no MySQL ou constantes no Laravel
 */
public enum UserProfile {

    STUDENT("Estudante", "Usuário que pode se inscrever em cursos"),
    INSTRUCTOR("Instrutor", "Usuário que pode criar e gerenciar cursos"),
    ADMIN("Administrador", "Usuário com acesso total ao sistema");

    private final String displayName;
    private final String description;

    // Construtor do enum
    UserProfile(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    // ===== GETTERS =====

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    // ===== MÉTODOS UTILITÁRIOS =====

    /**
     * Converte string para UserProfile (case insensitive)
     * Útil para APIs que recebem strings
     */
    public static UserProfile fromString(String profile) {
        if (profile == null) {
            return null;
        }

        for (UserProfile userProfile : UserProfile.values()) {
            if (userProfile.name().equalsIgnoreCase(profile)) {
                return userProfile;
            }
        }

        throw new IllegalArgumentException("Profile inválido: " + profile);
    }

    /**
     * Verifica se um usuário tem permissão para criar cursos
     */
    public boolean canCreateCourses() {
        return this == INSTRUCTOR || this == ADMIN;
    }

    /**
     * Verifica se um usuário tem permissões administrativas
     */
    public boolean hasAdminPermissions() {
        return this == ADMIN;
    }

    /**
     * Verifica se um usuário pode se inscrever em cursos
     */
    public boolean canEnrollInCourses() {
        return this == STUDENT || this == INSTRUCTOR;
    }

    @Override
    public String toString() {
        return displayName;
    }
}