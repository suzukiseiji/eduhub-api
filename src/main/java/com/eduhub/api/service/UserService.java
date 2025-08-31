package com.eduhub.api.service;

import com.eduhub.api.model.entity.User;
import com.eduhub.api.model.entity.UserProfile;
import com.eduhub.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * UserService - Lógica de negócio para usuários
 * 
 * Equivale aos Services no Laravel
 * Contém as regras de negócio, validações e operações complexas
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Para criptografar senhas

    // ===== CRUD BÁSICO =====

    /**
     * Criar novo usuário
     * Equivale ao método create() de um Service no Laravel
     */
    public User createUser(String name, String email, String password, UserProfile profile) {
        // Validar se email já existe
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já está em uso: " + email);
        }

        // Criar usuário
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Criptografar senha
        user.setProfile(profile);
        user.setActive(true);

        // Salvar no banco
        return userRepository.save(user);
    }

    /**
     * Buscar usuário por ID
     * Equivale: User::findOrFail($id)
     */
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    /**
     * Buscar usuário por email
     * Equivale: User::where('email', $email)->first()
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Listar todos os usuários com paginação
     * Equivale: User::paginate(10)
     */
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    /**
     * Atualizar usuário
     * Equivale ao método update() de um Service no Laravel
     */
    public User updateUser(String id, String name, String email, String phone, String bio) {
        User user = findById(id);

        // Verificar se email mudou e se já existe
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já está em uso: " + email);
        }

        // Atualizar campos
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setBio(bio);

        return userRepository.save(user);
    }

    /**
     * Deletar usuário (soft delete - marcar como inativo)
     */
    public void deleteUser(String id) {
        User user = findById(id);
        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Deletar usuário permanentemente
     */
    public void deleteUserPermanently(String id) {
        userRepository.deleteById(id);
    }

    // ===== MÉTODOS DE NEGÓCIO =====

    /**
     * Buscar instrutores ativos
     */
    public List<User> findActiveInstructors() {
        return userRepository.findActiveInstructors();
    }

    /**
     * Buscar usuários por perfil
     */
    public List<User> findByProfile(UserProfile profile) {
        return userRepository.findByProfile(profile);
    }

    /**
     * Pesquisar usuários por nome ou email
     */
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByNameOrEmailContaining(searchTerm);
    }

    /**
     * Contar usuários por perfil
     */
    public long countUsersByProfile(UserProfile profile) {
        return userRepository.countByProfile(profile);
    }

    /**
     * Verificar se usuário pode criar cursos
     */
    public boolean canCreateCourses(String userId) {
        User user = findById(userId);
        return user.getProfile().canCreateCourses();
    }

    /**
     * Ativar/desativar usuário
     */
    public User toggleUserStatus(String id) {
        User user = findById(id);
        user.setActive(!user.isActive());
        return userRepository.save(user);
    }

    /**
     * Alterar senha do usuário
     */
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        
        // Verificar senha atual (implementar verificação depois)
        // if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        //     throw new RuntimeException("Senha atual incorreta");
        // }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ===== ESTATÍSTICAS =====

    /**
     * Obter estatísticas de usuários
     */
    public UserStats getUserStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActive(true);
        long instructors = userRepository.countByProfile(UserProfile.INSTRUCTOR);
        long students = userRepository.countByProfile(UserProfile.STUDENT);
        long admins = userRepository.countByProfile(UserProfile.ADMIN);

        return new UserStats(totalUsers, activeUsers, instructors, students, admins);
    }

    // Classe interna para estatísticas
    public static class UserStats {
        public final long totalUsers;
        public final long activeUsers;
        public final long instructors;
        public final long students;
        public final long admins;

        public UserStats(long totalUsers, long activeUsers, long instructors, long students, long admins) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.instructors = instructors;
            this.students = students;
            this.admins = admins;
        }
    }
}