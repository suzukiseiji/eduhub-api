package com.eduhub.api.model.entity;

/**
 * Enum EnrollmentStatus - Status da inscrição
 */
public enum EnrollmentStatus {
    ACTIVE("Ativo", "Estudante está cursando"),
    COMPLETED("Concluído", "Estudante concluiu o curso"),
    SUSPENDED("Suspenso", "Inscrição temporariamente suspensa"),
    CANCELLED("Cancelado", "Inscrição foi cancelada"),
    EXPIRED("Expirado", "Inscrição expirou (cursos com prazo)"),
    PENDING_PAYMENT("Aguardando Pagamento", "Aguardando confirmação do pagamento");

    private final String displayName;
    private final String description;

    EnrollmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }

    public static EnrollmentStatus fromString(String status) {
        if (status == null) return null;
        for (EnrollmentStatus enrollmentStatus : EnrollmentStatus.values()) {
            if (enrollmentStatus.name().equalsIgnoreCase(status)) {
                return enrollmentStatus;
            }
        }
        throw new IllegalArgumentException("Status inválido: " + status);
    }

    /**
     * Verificar se é um status ativo (estudante pode acessar o curso)
     */
    public boolean isActive() {
        return this == ACTIVE || this == COMPLETED;
    }

    /**
     * Verificar se o estudante pode continuar estudando
     */
    public boolean canStudy() {
        return this == ACTIVE;
    }

    /**
     * Verificar se pode gerar certificado
     */
    public boolean canGenerateCertificate() {
        return this == COMPLETED;
    }

    @Override
    public String toString() { return displayName; }
}

