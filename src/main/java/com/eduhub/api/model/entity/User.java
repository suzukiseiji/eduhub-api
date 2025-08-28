package com.eduhub.api.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity User - Representa um usuário do sistema
 * 
 * Equivale ao Model User do Laravel
 * @Document = Model no Laravel  
 * @Id = Primary Key
 * @Indexed = Index no banco
 */
@Document(collection = "users") // Nome da collection no MongoDB
public class User {

    @Id
    private String id; // MongoDB usa String como ID por padrão

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Indexed(unique = true) // Email único (como no Laravel)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    private UserProfile profile; // ENUM: STUDENT, INSTRUCTOR, ADMIN

    private boolean active = true;

    // Campos de auditoria (equivale aos timestamps do Laravel)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Campos específicos para perfil
    private String phone;
    private String avatar;
    private String bio;

    // ===== CONSTRUTORES =====

    public User() {
        // Construtor padrão obrigatório
    }

    public User(String name, String email, String password, UserProfile profile) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.active = true;
    }

    // ===== GETTERS E SETTERS =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // ===== MÉTODOS UTILITÁRIOS =====

    /**
     * Verifica se o usuário é um instrutor
     */
    public boolean isInstructor() {
        return UserProfile.INSTRUCTOR.equals(this.profile);
    }

    /**
     * Verifica se o usuário é um administrador
     */
    public boolean isAdmin() {
        return UserProfile.ADMIN.equals(this.profile);
    }

    /**
     * Verifica se o usuário é um estudante
     */
    public boolean isStudent() {
        return UserProfile.STUDENT.equals(this.profile);
    }

    // ===== EQUALS E HASHCODE =====
    // Importante para comparações e coleções

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    // ===== TOSTRING =====
    // Para logs e debugging

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profile=" + profile +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}