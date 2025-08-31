package com.eduhub.api.controller;

import com.eduhub.api.model.entity.User;
import com.eduhub.api.model.entity.UserProfile;
import com.eduhub.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UserController - Endpoints para testar nossa implementação
 * 
 * Equivale aos Controllers no Laravel
 * Endpoints básicos para testar User, Repository e Service
 */
@RestController
@RequestMapping("/users") // Como routes no Laravel: Route::prefix('users')
public class UserController {

    @Autowired
    private UserService userService;

    // ===== ENDPOINTS PARA TESTE =====

    /**
     * GET /api/v1/users/test
     * Endpoint simples para testar se está funcionando
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ EduHub API funcionando! MongoDB conectado!");
    }

    /**
     * POST /api/v1/users/create-test
     * Criar usuário de teste
     */
    @PostMapping("/create-test")
    public ResponseEntity<User> createTestUser() {
        try {
            User user = userService.createUser(
                "João Silva",
                "joao.teste@eduhub.com",
                "123456",
                UserProfile.INSTRUCTOR
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/users
     * Listar todos os usuários
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // Aqui seria com paginação, mas para teste vamos buscar todos
        List<User> users = userService.findAll(null).getContent();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/{id}
     * Buscar usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/v1/users/email/{email}
     * Buscar usuário por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/v1/users/profile/{profile}
     * Buscar usuários por perfil
     */
    @GetMapping("/profile/{profile}")
    public ResponseEntity<List<User>> getUsersByProfile(@PathVariable String profile) {
        try {
            UserProfile userProfile = UserProfile.fromString(profile);
            List<User> users = userService.findByProfile(userProfile);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/users/search?term=texto
     * Pesquisar usuários por nome ou email
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String term) {
        List<User> users = userService.searchUsers(term);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1/users/stats
     * Estatísticas de usuários
     */
    @GetMapping("/stats")
    public ResponseEntity<UserService.UserStats> getUserStats() {
        UserService.UserStats stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /api/v1/users/seed
     * Criar dados de teste (seed)
     */
    @PostMapping("/seed")
    public ResponseEntity<Map<String, String>> seedUsers() {
        try {
            // Criar alguns usuários para teste
            userService.createUser("Admin Sistema", "admin@eduhub.com", "admin123", UserProfile.ADMIN);
            userService.createUser("Maria Instrutora", "maria@eduhub.com", "123456", UserProfile.INSTRUCTOR);
            userService.createUser("Pedro Estudante", "pedro@eduhub.com", "123456", UserProfile.STUDENT);
            userService.createUser("Ana Estudante", "ana@eduhub.com", "123456", UserProfile.STUDENT);

            return ResponseEntity.ok(Map.of("message", "✅ Usuários de teste criados com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "⚠️ Alguns usuários já existem: " + e.getMessage()));
        }
    }
}