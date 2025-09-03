package com.eduhub.api.controller;

import com.eduhub.api.model.entity.Course;
import com.eduhub.api.model.entity.CourseCategory;
import com.eduhub.api.model.entity.CourseLevel;
import com.eduhub.api.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CourseController - Endpoints para testar cursos
 *
 * Equivale aos Controllers no Laravel
 * Endpoints para CRUD e operações com cursos
 */
@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // ===== ENDPOINTS BÁSICOS =====

    /**
     * GET /api/v1/courses/test
     * Teste básico
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ CourseController funcionando!");
    }

    /**
     * POST /api/v1/courses/seed
     * Criar cursos de teste
     */
    @PostMapping("/seed")
    public ResponseEntity<Map<String, String>> seedCourses() {
        try {
            // Primeiro, vamos buscar um instrutor existente
            // Supondo que você já rodou o /users/seed

            // IMPORTANTE: Você precisa trocar este ID pelo ID real de um instrutor
            // Pegue do endpoint GET /users/profile/INSTRUCTOR
            String instructorId = "68b491f9c23c4d5850c857da";

            // Criar cursos de teste
            courseService.createCourse(
                    "Java Fundamentals",
                    "Curso completo de Java para iniciantes, cobrindo desde sintaxe básica até programação orientada a objetos.",
                    CourseCategory.PROGRAMMING,
                    CourseLevel.BEGINNER,
                    instructorId,
                    199.90
            );

            courseService.createCourse(
                    "Spring Boot Avançado",
                    "Desenvolvimento de APIs REST com Spring Boot, incluindo segurança, testes e deploy.",
                    CourseCategory.PROGRAMMING,
                    CourseLevel.ADVANCED,
                    instructorId,
                    299.90
            );

            courseService.createCourse(
                    "MongoDB para Desenvolvedores",
                    "Banco de dados NoSQL MongoDB: modelagem, queries, agregações e performance.",
                    CourseCategory.DATA_SCIENCE,
                    CourseLevel.INTERMEDIATE,
                    instructorId,
                    179.90
            );

            courseService.createCourse(
                    "Introdução ao Git",
                    "Controle de versão com Git e GitHub para iniciantes.",
                    CourseCategory.PROGRAMMING,
                    CourseLevel.BEGINNER,
                    instructorId,
                    0.0 // Curso gratuito
            );

            return ResponseEntity.ok(Map.of("message", "✅ Cursos de teste criados com sucesso!"));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "message", "⚠️ Erro ao criar cursos: " + e.getMessage(),
                    "dica", "Execute primeiro: POST /users/seed para criar instrutores"
            ));
        }
    }

    // ===== LISTAGEM =====

    /**
     * GET /api/v1/courses
     * Listar todos os cursos
     */
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.findAllSimple();
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/v1/courses/active
     * Listar apenas cursos ativos
     */
    @GetMapping("/active")
    public ResponseEntity<List<Course>> getActiveCourses() {
        List<Course> courses = courseService.findActiveCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/v1/courses/{id}
     * Buscar curso por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        try {
            Course course = courseService.findById(id);
            return ResponseEntity.ok(course);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== BUSCA POR FILTROS =====

    /**
     * GET /api/v1/courses/category/{category}
     * Buscar cursos por categoria
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Course>> getCoursesByCategory(@PathVariable String category) {
        try {
            CourseCategory courseCategory = CourseCategory.fromString(category);
            List<Course> courses = courseService.findByCategory(courseCategory);
            return ResponseEntity.ok(courses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/courses/level/{level}
     * Buscar cursos por nível
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<List<Course>> getCoursesByLevel(@PathVariable String level) {
        try {
            CourseLevel courseLevel = CourseLevel.fromString(level);
            List<Course> courses = courseService.findByLevel(courseLevel);
            return ResponseEntity.ok(courses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/v1/courses/instructor/{instructorId}
     * Buscar cursos por instrutor
     */
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Course>> getCoursesByInstructor(@PathVariable String instructorId) {
        List<Course> courses = courseService.findByInstructor(instructorId);
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/v1/courses/free
     * Buscar cursos gratuitos
     */
    @GetMapping("/free")
    public ResponseEntity<List<Course>> getFreeCourses() {
        List<Course> courses = courseService.findFreeCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/v1/courses/premium
     * Buscar cursos pagos
     */
    @GetMapping("/premium")
    public ResponseEntity<List<Course>> getPremiumCourses() {
        List<Course> courses = courseService.findPremiumCourses();
        return ResponseEntity.ok(courses);
    }

    // ===== PESQUISA =====

    /**
     * GET /api/v1/courses/search?term=java
     * Pesquisar cursos por termo
     */
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String term) {
        List<Course> courses = courseService.searchCourses(term);
        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/v1/courses/price-range?min=0&max=200
     * Buscar cursos por faixa de preço
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<Course>> getCoursesByPriceRange(
            @RequestParam(defaultValue = "0") double min,
            @RequestParam(defaultValue = "1000") double max) {
        List<Course> courses = courseService.findByPriceRange(min, max);
        return ResponseEntity.ok(courses);
    }

    // ===== ESTATÍSTICAS =====

    /**
     * GET /api/v1/courses/stats
     * Estatísticas de cursos
     */
    @GetMapping("/stats")
    public ResponseEntity<CourseService.CourseStats> getCourseStats() {
        CourseService.CourseStats stats = courseService.getCourseStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/v1/courses/stats/category/{category}
     * Contar cursos por categoria
     */
    @GetMapping("/stats/category/{category}")
    public ResponseEntity<Map<String, Object>> getStatsByCategory(@PathVariable String category) {
        try {
            CourseCategory courseCategory = CourseCategory.fromString(category);
            long count = courseService.countByCategory(courseCategory);

            return ResponseEntity.ok(Map.of(
                    "category", courseCategory.getDisplayName(),
                    "count", count
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}