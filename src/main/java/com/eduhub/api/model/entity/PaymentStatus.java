package com.eduhub.api.model.entity;

/**
 * Enum PaymentStatus - Status do pagamento
 */
public enum PaymentStatus {
    PENDING("Pendente", "Aguardando pagamento"),
    PAID("Pago", "Pagamento confirmado"),
    FAILED("Falhou", "Pagamento não foi aprovado"),
    REFUNDED("Reembolsado", "Pagamento foi estornado"),
    CANCELLED("Cancelado", "Pagamento foi cancelado"),
    FREE("Gratuito", "Curso gratuito - não requer pagamento");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentStatus fromString(String status) {
        if (status == null) return null;
        for (PaymentStatus paymentStatus : PaymentStatus.values()) {
            if (paymentStatus.name().equalsIgnoreCase(status)) {
                return paymentStatus;
            }
        }
        throw new IllegalArgumentException("Status de pagamento inválido: " + status);
    }

    /**
     * Verificar se pagamento foi aprovado
     */
    public boolean isPaid() {
        return this == PAID || this == FREE;
    }

    /**
     * Verificar se pode acessar o curso
     */
    public boolean allowsAccess() {
        return this == PAID || this == FREE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
