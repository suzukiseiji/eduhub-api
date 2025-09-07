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

            String studentId1 = "68b7af94c603f8688f154b36"; // Trocar pelo ID real
            String studentId2 = "68b7af94c603f8688f154b37"; // Trocar pelo ID real
            String courseId1 = "68b7afebc603f8688f154b3a";      // Trocar pelo ID real
            String courseId2 = "68b7afebc603f8688f154b3b";      // Trocar pelo ID real

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

    // ===== PROGRESSO =====

    /**
     * POST /api/v1/enrollments/{enrollmentId}/complete-lesson
     * Marcar aula como completada
     */
    @PostMapping("/{enrollmentId}/complete-lesson")
    public ResponseEntity<Enrollment> completeLesson(
            @PathVariable String enrollmentId,
            @RequestParam String moduleTitle,
            @RequestParam String lessonTitle,
            @RequestParam(defaultValue = "1") int lessonOrder,
            @RequestParam(defaultValue = "0") int timeSpent) {
        try {
            Enrollment enrollment = enrollmentService.completeLesson(
                    enrollmentId, moduleTitle, lessonTitle, lessonOrder, timeSpent);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/v1/enrollments/{enrollmentId}/complete-course
     * Finalizar curso e gerar certificado
     */
    @PostMapping("/{enrollmentId}/complete-course")
    public ResponseEntity<Enrollment> completeCourse(@PathVariable String enrollmentId) {
        try {
            Enrollment enrollment = enrollmentService.completeCourse(enrollmentId);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== VERIFICAÇÕES =====

    /**
     * GET /api/v1/enrollments/check?studentId=X&courseId=Y
     * Verificar se estudante está inscrito em curso
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkEnrollment(
            @RequestParam String studentId,
            @RequestParam String courseId) {
        boolean isEnrolled = enrollmentService.isStudentEnrolled(studentId, courseId);

        Map<String, Object> response = Map.of(
                "studentId", studentId,
                "courseId", courseId,
                "isEnrolled", isEnrolled
        );

        return ResponseEntity.ok(response);
    }

    // ===== AVALIAÇÕES =====

    /**
     * POST /api/v1/enrollments/{enrollmentId}/rate
     * Avaliar curso
     */
    @PostMapping("/{enrollmentId}/rate")
    public ResponseEntity<Enrollment> rateCourse(
            @PathVariable String enrollmentId,
            @RequestParam int rating,
            @RequestParam(required = false) String comment) {
        try {
            Enrollment enrollment = enrollmentService.rateCourse(enrollmentId, rating, comment);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== PAGAMENTOS =====

    /**
     * POST /api/v1/enrollments/{enrollmentId}/confirm-payment
     * Confirmar pagamento
     */
    @PostMapping("/{enrollmentId}/confirm-payment")
    public ResponseEntity<Enrollment> confirmPayment(
            @PathVariable String enrollmentId,
            @RequestParam String transactionId,
            @RequestParam String paymentMethod) {
        try {
            Enrollment enrollment = enrollmentService.confirmPayment(enrollmentId, transactionId, paymentMethod);
            return ResponseEntity.ok(enrollment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ===== CONTROLE DE ACESSO =====

    /**
     * POST /api/v1/enrollments/{enrollmentId}/update-access
     * Atualizar último acesso
     */
    @PostMapping("/{enrollmentId}/update-access")
    public ResponseEntity<Enrollment> updateLastAccess(@PathVariable String enrollmentId) {
        Enrollment enrollment = enrollmentService.updateLastAccess(enrollmentId);
        return ResponseEntity.ok(enrollment);
    }

    /**
     * POST /api/v1/enrollments/{enrollmentId}/suspend
     * Suspender inscrição
     */
    @PostMapping("/{enrollmentId}/suspend")
    public ResponseEntity<Enrollment> suspendEnrollment(
            @PathVariable String enrollmentId,
            @RequestParam String reason) {
        Enrollment enrollment = enrollmentService.suspendEnrollment(enrollmentId, reason);
        return ResponseEntity.ok(enrollment);
    }

    /**
     * POST /api/v1/enrollments/{enrollmentId}/reactivate
     * Reativar inscrição
     */
    @PostMapping("/{enrollmentId}/reactivate")
    public ResponseEntity<Enrollment> reactivateEnrollment(@PathVariable String enrollmentId) {
        Enrollment enrollment = enrollmentService.reactivateEnrollment(enrollmentId);
        return ResponseEntity.ok(enrollment);
    }

    /**
     * DELETE /api/v1/enrollments/{enrollmentId}
     * Cancelar inscrição
     */
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Map<String, String>> cancelEnrollment(@PathVariable String enrollmentId) {
        try {
            enrollmentService.cancelEnrollment(enrollmentId);
            return ResponseEntity.ok(Map.of("message", "✅ Inscrição cancelada com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== ESTATÍSTICAS =====

    /**
     * GET /api/v1/enrollments/stats
     * Estatísticas gerais de inscrições
     */
    @GetMapping("/stats")
    public ResponseEntity<EnrollmentService.EnrollmentStats> getEnrollmentStats() {
        EnrollmentService.EnrollmentStats stats = enrollmentService.getEnrollmentStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/v1/enrollments/stats/course/{courseId}
     * Estatísticas de um curso específico
     */
    @GetMapping("/stats/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseStats(@PathVariable String courseId) {
        long totalEnrollments = enrollmentService.countEnrollmentsByCourse(courseId);
        long activeEnrollments = enrollmentService.countActiveEnrollmentsByCourse(courseId);

        return ResponseEntity.ok(Map.of(
                "courseId", courseId,
                "totalEnrollments", totalEnrollments,
                "activeEnrollments", activeEnrollments,
                "completionRate", activeEnrollments > 0 ?
                        String.format("%.1f%%", (double)(totalEnrollments - activeEnrollments) / totalEnrollments * 100) : "0%"
        ));
    }

    // ===== RELATÓRIOS =====

    /**
     * GET /api/v1/enrollments/inactive-students?days=30
     * Buscar estudantes inativos
     */
    @GetMapping("/inactive-students")
    public ResponseEntity<List<Enrollment>> getInactiveStudents(
            @RequestParam(defaultValue = "30") int days) {
        List<Enrollment> inactiveStudents = enrollmentService.findInactiveStudents(days);
        return ResponseEntity.ok(inactiveStudents);
    }

    /**
     * GET /api/v1/enrollments/low-progress?maxProgress=20
     * Buscar estudantes com baixo progresso
     */
    @GetMapping("/low-progress")
    public ResponseEntity<List<Enrollment>> getStudentsWithLowProgress(
            @RequestParam(defaultValue = "20") double maxProgress) {
        List<Enrollment> lowProgressStudents = enrollmentService.findStudentsWithLowProgress(maxProgress);
        return ResponseEntity.ok(lowProgressStudents);
    }

    /**
     * GET /api/v1/enrollments/recent?days=7
     * Buscar inscrições recentes
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Enrollment>> getRecentEnrollments(
            @RequestParam(defaultValue = "7") int days) {
        List<Enrollment> recentEnrollments = enrollmentService.findRecentEnrollments(days);
        return ResponseEntity.ok(recentEnrollments);
    }
}