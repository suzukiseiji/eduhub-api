package com.eduhub.api.repository;

import com.eduhub.api.model.entity.Course;
import com.eduhub.api.model.entity.CourseCategory;
import com.eduhub.api.model.entity.CourseLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CourseRepository - Interface para acesso aos dados de cursos
 * 
 * Métodos para buscar cursos por categoria, instrutor, preço, etc.
 * Equivale aos scopes do Eloquent no Laravel
 */
@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    // ===== BUSCA BÁSICA =====

    /**
     * Buscar curso por título (case insensitive)
     * Equivale: Course::where('title', 'LIKE', '%$title%')->first()
     */
    Optional<Course> findByTitleIgnoreCase(String title);

    /**
     * Verificar se curso com título já existe
     * Equivale: Course::where('title', $title)->exists()
     */
    boolean existsByTitleIgnoreCase(String title);

    // ===== BUSCA POR CATEGORIA =====

    /**
     * Buscar cursos por categoria
     * Equivale: Course::where('category', $category)->get()
     */
    List<Course> findByCategory(CourseCategory category);

    /**
     * Buscar cursos por categoria com paginação
     * Equivale: Course::where('category', $category)->paginate(10)
     */
    Page<Course> findByCategory(CourseCategory category, Pageable pageable);

    /**
     * Buscar cursos ativos por categoria
     * Equivale: Course::where('category', $category)->where('active', true)->get()
     */
    List<Course> findByCategoryAndActive(CourseCategory category, boolean active);

    // ===== BUSCA POR NÍVEL =====

    /**
     * Buscar cursos por nível
     * Equivale: Course::where('level', $level)->get()
     */
    List<Course> findByLevel(CourseLevel level);

    /**
     * Buscar cursos por categoria e nível
     * Equivale: Course::where('category', $cat)->where('level', $level)->get()
     */
    List<Course> findByCategoryAndLevel(CourseCategory category, CourseLevel level);

    // ===== BUSCA POR INSTRUTOR =====

    /**
     * Buscar cursos por ID do instrutor
     * Equivale: Course::where('instructor_id', $id)->get()
     */
    @Query("{'instructor.id': ?0}")
    List<Course> findByInstructorId(String instructorId);

    /**
     * Buscar cursos por nome do instrutor
     * Equivale: Course::where('instructor_name', 'LIKE', '%$name%')->get()
     */
    @Query("{'instructor.name': {'$regex': ?0, '$options': 'i'}}")
    List<Course> findByInstructorNameContaining(String instructorName);

    /**
     * Buscar cursos ativos por instrutor
     */
    @Query("{'instructor.id': ?0, 'active': true}")
    List<Course> findActiveByInstructorId(String instructorId);

    // ===== BUSCA POR PREÇO =====

    /**
     * Buscar cursos por faixa de preço
     * Equivale: Course::whereBetween('price', [$min, $max])->get()
     */
    List<Course> findByPriceBetween(double minPrice, double maxPrice);

    /**
     * Buscar cursos até determinado preço
     * Equivale: Course::where('price', '<=', $maxPrice)->get()
     */
    List<Course> findByPriceLessThanEqual(double maxPrice);

    /**
     * Buscar cursos gratuitos
     * Equivale: Course::where('price', 0)->get()
     */
    List<Course> findByPrice(double price);

    // ===== BUSCA POR TEXTO =====

    /**
     * Buscar cursos por título ou descrição
     * Equivale: Course::where('title', 'LIKE', '%$term%')->orWhere('description', 'LIKE', '%$term%')->get()
     */
    @Query("{'$or': [" +
           "{'title': {'$regex': ?0, '$options': 'i'}}, " +
           "{'description': {'$regex': ?0, '$options': 'i'}}" +
           "]}")
    List<Course> findByTitleOrDescriptionContaining(String searchTerm);

    /**
     * Busca completa: título, descrição ou instrutor
     */
    @Query("{'$or': [" +
           "{'title': {'$regex': ?0, '$options': 'i'}}, " +
           "{'description': {'$regex': ?0, '$options': 'i'}}, " +
           "{'instructor.name': {'$regex': ?0, '$options': 'i'}}" +
           "]}")
    List<Course> searchCourses(String searchTerm);

    // ===== BUSCA POR STATUS =====

    /**
     * Buscar cursos ativos
     * Equivale: Course::where('active', true)->get()
     */
    List<Course> findByActive(boolean active);

    /**
     * Buscar cursos ativos com paginação
     */
    Page<Course> findByActive(boolean active, Pageable pageable);

    // ===== BUSCA POR DATA =====

    /**
     * Buscar cursos criados após data
     * Equivale: Course::where('created_at', '>', $date)->get()
     */
    List<Course> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Buscar cursos criados entre datas
     * Equivale: Course::whereBetween('created_at', [$start, $end])->get()
     */
    List<Course> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // ===== QUERIES COMPLEXAS =====

    /**
     * Buscar cursos populares (ordenados por criação)
     * Equivale: Course::where('active', true)->latest()->limit($limit)->get()
     */
    List<Course> findTop10ByActiveOrderByCreatedAtDesc(boolean active);

    /**
     * Buscar cursos por múltiplas categorias
     * Equivale: Course::whereIn('category', $categories)->get()
     */
    List<Course> findByCategoryIn(List<CourseCategory> categories);

    /**
     * Buscar cursos por múltiplos níveis
     * Equivale: Course::whereIn('level', $levels)->get()
     */
    List<Course> findByLevelIn(List<CourseLevel> levels);

    /**
     * Cursos gratuitos ativos
     */
    @Query("{'price': 0, 'active': true}")
    List<Course> findFreeCourses();

    /**
     * Cursos premium (pagos) ativos
     */
    @Query("{'price': {'$gt': 0}, 'active': true}")
    List<Course> findPremiumCourses();

    /**
     * Buscar cursos com desconto (preço menor que X)
     * Query customizada
     */
    @Query("{'active': true, 'price': {'$gte': ?0, '$lte': ?1}}")
    List<Course> findCoursesInPriceRange(double minPrice, double maxPrice);

    // ===== AGREGAÇÕES E ESTATÍSTICAS =====

    /**
     * Contar cursos por categoria
     * Equivale: Course::where('category', $category)->count()
     */
    long countByCategory(CourseCategory category);

    /**
     * Contar cursos por instrutor
     */
    @Query(value = "{'instructor.id': ?0}", count = true)
    long countByInstructorId(String instructorId);

    /**
     * Contar cursos ativos
     * Equivale: Course::where('active', true)->count()
     */
    long countByActive(boolean active);

    /**
     * Contar cursos por nível
     */
    long countByLevel(CourseLevel level);

    // ===== FILTROS COMBINADOS =====

    /**
     * Buscar cursos ativos por categoria e nível
     */
    List<Course> findByActiveAndCategoryAndLevel(boolean active, CourseCategory category, CourseLevel level);

    /**
     * Buscar cursos ativos em faixa de preço por categoria
     */
    @Query("{'active': true, 'category': ?0, 'price': {'$gte': ?1, '$lte': ?2}}")
    List<Course> findActiveCoursesByCategoryAndPriceRange(CourseCategory category, double minPrice, double maxPrice);

    // ===== ORDENAÇÃO =====

    /**
     * Buscar cursos ordenados por preço (crescente)
     * Equivale: Course::orderBy('price')->get()
     */
    List<Course> findByActiveOrderByPriceAsc(boolean active);

    /**
     * Buscar cursos ordenados por data de criação (mais recentes)
     * Equivale: Course::latest()->get()
     */
    List<Course> findByActiveOrderByCreatedAtDesc(boolean active);

    /**
     * Buscar cursos ordenados por título
     * Equivale: Course::orderBy('title')->get()
     */
    List<Course> findByActiveOrderByTitleAsc(boolean active);
}