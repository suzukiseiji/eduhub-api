package com.eduhub.api.controller;

import com.eduhub.api.model.entity.Enrollment;
import com.eduhub.api.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * EnrollmentController - Endpoints para testar inscrições
 * 
 * Gerencia relacionamento entre estudantes e cursos
 * Equivale aos Controllers de relacionamento do Laravel
 */
@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // ===== ENDPOINTS BÁSICOS =====

    /**
     * GET /api/v1/enrollments/test
     * Teste básico
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ EnrollmentController funcionando!");
    }

    /**
     * POST /api/v1/enrollments/seed
     * Criar inscrições de teste
     */
    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> seedEnrollments() {
        try {
            // IMPORTANTE: Você precisa substituir pelos IDs reais
            // Pegue dos endpoints:
            // GET /users/profile/STUDENT - para IDs de estudantes
            // GET /courses - para IDs de cursos

            String studentId1 = "ID_ESTUDANTE_1"; // Trocar pelo ID real
            String studentId2 = "ID_ESTUDANTE_2"; // Trocar pelo ID real
            String courseId1 = "ID_CURSO_1";      // Trocar pelo ID real
            String courseId2 = "ID_CURSO_2";      // Trocar pelo ID real

            // Criar inscrições de teste
            Enrollment enrollment1 = enrollmentService.enrollStudent(studentId1, courseId1);
            Enrollment enrollment2 = enrollmentService.enrollStudent(studentId2, courseId1); // Mesmo curso
            Enrollment enrollment3 = enrollmentService.enrollStudent(studentId1, courseId2); // Mesmo estudante

            // Simular progresso em algumas inscrições
            enrollmentService.completeLesson(
                    enrollment1.getId(),
                    "Módulo 1",
                    "Aula 1 - Introdução",
                    1,
                    1800
            );

            return ResponseEntity.ok(Map.of(
                    "message", "✅ Inscrições de teste criadas!",
                    "enrollments", List.of(
                            enrollment1.getId(),
                            enrollment2.getId(),
                            enrollment3.getId()
                    ),
                    "dica", "Use os IDs retornados para testar outros endpoints"
            ));

        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "message", "⚠️ Erro ao criar inscrições: " + e.getMessage(),
                    "dica", "Execute primeiro: POST /users/seed e POST /courses/seed"
            ));
        }
    }

    // ===== INSCRIÇÃO =====

    /**
     * POST /api/v1/enrollments/enroll
     * Inscrever estudante em curso
     */
    @PostMapping("/enroll")
    public ResponseEntity<Enrollment> enrollStudent(
            @RequestParam String studentId,
            @RequestParam String courseId) {
        try {
            Enrollment enrollment = enrollmentService.enrollStudent(studentId, courseId);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== LISTAGEM =====

    /**
     * GET /api/v1/enrollments/student/{studentId}
     * Buscar inscrições de um estudante
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudent(@PathVariable String studentId) {
        List<Enrollment> enrollments = enrollmentService.findByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * GET /api/v1/enrollments/student/{studentId}/active
     * Buscar cursos ativos de um estudante
     */
    @GetMapping("/student/{studentId}/active")
    public ResponseEntity<List<Enrollment>> getActiveEnrollmentsByStudent(@PathVariable String studentId) {
        List<Enrollment> enrollments = enrollmentService.findActiveByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * GET /api/v1/enrollments/student/{studentId}/completed
     * Buscar cursos concluídos de um estudante
     */
    @GetMapping("/student/{studentId}/completed")
    public ResponseEntity<List<Enrollment>> getCompletedEnrollmentsByStudent(@PathVariable String studentId) {
        List<Enrollment> enrollments = enrollmentService.findCompletedByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * GET /api/v1/enrollments/course/{courseId}
     * Buscar estudantes de um curso
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourse(@PathVariable String courseId) {
        List<Enrollment> enrollments = enrollmentService.findByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    /**
     * GET /api/v1/enrollments/{id}
     * Buscar inscrição específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Enrollment> getEnrollmentById(@PathVariable String id) {
        try {
            Enrollment enrollment = enrollmentService.findById(id);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}