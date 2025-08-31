package com.eduhub.api.repository;

import com.eduhub.api.model.entity.User;
import com.eduhub.api.model.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * UserRepository - Interface para acesso aos dados de usuários
 * 
 * Equivale aos métodos do Eloquent no Laravel:
 * - User::find(), User::where(), User::create(), etc.
 * 
 * MongoRepository<User, String> = Eloquent Model
 * - User: Entity que estamos manipulando
 * - String: Tipo do ID (ObjectId como String)
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // ===== BUSCA POR CAMPOS ÚNICOS =====
    
    /**
     * Buscar usuário por email (único)
     * Equivale: User::where('email', $email)->first()
     */
    Optional<User> findByEmail(String email);

    /**
     * Verificar se email já existe
     * Equivale: User::where('email', $email)->exists()
     */
    boolean existsByEmail(String email);

    // ===== BUSCA POR PERFIL =====
    
    /**
     * Buscar usuários por perfil
     * Equivale: User::where('profile', $profile)->get()
     */
    List<User> findByProfile(UserProfile profile);

    /**
     * Buscar usuários por perfil com paginação
     * Equivale: User::where('profile', $profile)->paginate(10)
     */
    Page<User> findByProfile(UserProfile profile, Pageable pageable);

    /**
     * Contar usuários por perfil
     * Equivale: User::where('profile', $profile)->count()
     */
    long countByProfile(UserProfile profile);

    // ===== BUSCA POR STATUS =====
    
    /**
     * Buscar usuários ativos
     * Equivale: User::where('active', true)->get()
     */
    List<User> findByActive(boolean active);

    /**
     * Buscar usuários ativos por perfil
     * Equivale: User::where('active', true)->where('profile', $profile)->get()
     */
    List<User> findByActiveAndProfile(boolean active, UserProfile profile);

    // ===== BUSCA POR TEXTO =====
    
    /**
     * Buscar usuários por nome (contém)
     * Equivale: User::where('name', 'LIKE', '%$name%')->get()
     */
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Buscar usuários por nome ou email
     * Equivale: User::where('name', 'LIKE', '%$term%')->orWhere('email', 'LIKE', '%$term%')->get()
     */
    @Query("{'$or': [{'name': {'$regex': ?0, '$options': 'i'}}, {'email': {'$regex': ?0, '$options': 'i'}}]}")
    List<User> findByNameOrEmailContaining(String searchTerm);

    // ===== BUSCA POR DATA =====
    
    /**
     * Buscar usuários criados após uma data
     * Equivale: User::where('created_at', '>', $date)->get()
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Buscar usuários criados entre datas
     * Equivale: User::whereBetween('created_at', [$start, $end])->get()
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // ===== QUERIES CUSTOMIZADAS =====
    
    /**
     * Buscar instrutores ativos
     * Query customizada usando anotação @Query
     */
    @Query("{'profile': 'INSTRUCTOR', 'active': true}")
    List<User> findActiveInstructors();

    /**
     * Buscar usuários por múltiplos perfis
     * Equivale: User::whereIn('profile', $profiles)->get()
     */
    List<User> findByProfileIn(List<UserProfile> profiles);

    /**
     * Buscar top usuários por data de criação
     * Equivale: User::latest()->limit($limit)->get()
     */
    List<User> findTop10ByOrderByCreatedAtDesc();

    // ===== MÉTODOS DE CONTAGEM =====
    
    /**
     * Contar usuários ativos
     * Equivale: User::where('active', true)->count()
     */
    long countByActive(boolean active);

    /**
     * Contar usuários por perfil e status
     * Equivale: User::where('profile', $profile)->where('active', $active)->count()
     */
    long countByProfileAndActive(UserProfile profile, boolean active);

    // ===== EXCLUSÃO =====
    
    /**
     * Deletar usuários inativos
     * Equivale: User::where('active', false)->delete()
     */
    void deleteByActive(boolean active);
}