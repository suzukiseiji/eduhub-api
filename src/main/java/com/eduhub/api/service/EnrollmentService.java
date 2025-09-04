package com.eduhub.api.service;

import com.eduhub.api.model.entity.*;
import com.eduhub.api.repository.EnrollmentRepository;
import com.eduhub.api.repository.UserRepository;
import com.eduhub.api.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * EnrollmentService - Lógica de negócio para inscrições
 * 
 * Gerencia todo o processo de inscrição, progresso e conclusão de cursos
 * Equivale aos Services do Laravel para relacionamentos many-to-many
 */
@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    // ===== OPERAÇÕES DE INSCRIÇÃO =====

    /**
     * Inscrever estudante em um curso
     * Equivale: $user->courses()->attach($courseId)
     */
    public Enrollment enrollStudent(String studentId, String courseId) {
        // Buscar usuário e curso
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Estudante não encontrado: " + studentId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado: " + courseId));

        // Validações de negócio
        validateEnrollment(student, course);

        // Verificar se já está inscrito
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("Estudante já está inscrito neste curso");
        }

        // Criar inscrição
        Enrollment enrollment = new Enrollment(student, course);
        
        // Se curso for pago, marcar pagamento como pendente
        if (course.getPrice() > 0) {
            enrollment.setStatus(EnrollmentStatus.PENDING_PAYMENT);
        }

        return enrollmentRepository.save(enrollment);
    }

    /**
     * Validar se estudante pode se inscrever no curso
     */
    private void validateEnrollment(User student, Course course) {
        // Verificar se é estudante ou instrutor (instrutores podem estudar também)
        if (!student.getProfile().canEnrollInCourses()) {
            throw new RuntimeException("Usuário não pode se inscrever em cursos");
        }

        // Verificar se curso está ativo
        if (!course.isActive()) {
            throw new RuntimeException("Curso não está ativo para inscrições");
        }

        // Verificar se estudante não é o próprio instrutor do curso
        if (course.getInstructor().getId().equals(student.getId())) {
            throw new RuntimeException("Instrutor não pode se inscrever no próprio curso");
        }
    }

    /**
     * Confirmar pagamento e ativar inscrição
     */
    public Enrollment confirmPayment(String enrollmentId, String transactionId, String paymentMethod) {
        Enrollment enrollment = findById(enrollmentId);

        if (enrollment.getStatus() != EnrollmentStatus.PENDING_PAYMENT) {
            throw new RuntimeException("Inscrição não está pendente de pagamento");
        }

        // Atualizar pagamento
        Enrollment.PaymentInfo payment = new Enrollment.PaymentInfo(
            enrollment.getCourse().getPrice(), 
            paymentMethod, 
            transactionId
        );
        enrollment.setPayment(payment);
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        return enrollmentRepository.save(enrollment);
    }

    // ===== GERENCIAMENTO DE PROGRESSO =====

    /**
     * Marcar aula como completada
     */
    public Enrollment completeLesson(String enrollmentId, String moduleTitle, 
                                   String lessonTitle, int lessonOrder, int timeSpent) {
        Enrollment enrollment = findById(enrollmentId);

        // Verificar se pode estudar
        if (!enrollment.getStatus().canStudy()) {
            throw new RuntimeException("Não é possível completar aula - inscrição não está ativa");
        }

        // Verificar se aula já foi completada
        if (enrollment.isLessonCompleted(lessonTitle)) {
            throw new RuntimeException("Aula já foi completada anteriormente");
        }

        // Marcar como completada
        enrollment.completeLesson(moduleTitle, lessonTitle, lessonOrder);
        
        // Atualizar tempo gasto (se fornecido)
        if (timeSpent > 0 && !enrollment.getCompletedLessons().isEmpty()) {
            enrollment.getCompletedLessons()
                .get(enrollment.getCompletedLessons().size() - 1)
                .setTimeSpent(timeSpent);
        }

        return enrollmentRepository.save(enrollment);
    }

    /**
     * Atualizar progresso manualmente (se necessário)
     */
    public Enrollment updateProgress(String enrollmentId) {
        Enrollment enrollment = findById(enrollmentId);
        enrollment.updateProgress();
        return enrollmentRepository.save(enrollment);
    }

    /**
     * Finalizar curso e gerar certificado
     */
    public Enrollment completeCourse(String enrollmentId) {
        Enrollment enrollment = findById(enrollmentId);

        if (!enrollment.isCompleted()) {
            throw new RuntimeException("Curso ainda não foi totalmente completado");
        }

        // Gerar ID do certificado
        String certificateId = "CERT-" + System.currentTimeMillis() + "-" + enrollmentId.substring(0, 8);
        String certificateUrl = "/certificates/" + certificateId + ".pdf";

        enrollment.generateCertificate(certificateId, certificateUrl);

        return enrollmentRepository.save(enrollment);
    }

    // ===== BUSCA E CONSULTA =====

    /**
     * Buscar inscrição por ID
     */
    public Enrollment findById(String id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada: " + id));
    }

    /**
     * Buscar inscrições de um estudante
     * Equivale: $user->enrollments
     */
    public List<Enrollment> findByStudent(String studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    /**
     * Buscar cursos ativos de um estudante
     */
    public List<Enrollment> findActiveByStudent(String studentId) {
        return enrollmentRepository.findActiveByStudentId(studentId);
    }

    /**
     * Buscar cursos concluídos de um estudante
     */
    public List<Enrollment> findCompletedByStudent(String studentId) {
        return enrollmentRepository.findCompletedByStudentId(studentId);
    }

    /**
     * Buscar estudantes de um curso
     * Equivale: $course->enrollments
     */
    public List<Enrollment> findByCourse(String courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    /**
     * Verificar se estudante está inscrito
     */
    public boolean isStudentEnrolled(String studentId, String courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    /**
     * Buscar inscrição específica
     */
    public Optional<Enrollment> findEnrollment(String studentId, String courseId) {
        return enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    // ===== AVALIAÇÕES E FEEDBACK =====

    /**
     * Avaliar curso
     */
    public Enrollment rateCourse(String enrollmentId, int rating, String comment) {
        Enrollment enrollment = findById(enrollmentId);

        // Verificar se pode avaliar (deve ter progresso significativo)
        if (enrollment.getProgressPercentage() < 20) {
            throw new RuntimeException("É necessário ter pelo menos 20% de progresso para avaliar");
        }

        // Validar rating
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Avaliação deve ser entre 1 e 5 estrelas");
        }

        enrollment.rateCourse(rating, comment);
        return enrollmentRepository.save(enrollment);
    }

    // ===== CONTROLE DE ACESSO =====

    /**
     * Atualizar último acesso
     */
    public Enrollment updateLastAccess(String enrollmentId) {
        Enrollment enrollment = findById(enrollmentId);
        enrollment.setLastAccessedAt(LocalDateTime.now());
        return enrollmentRepository.save(enrollment);
    }

    /**
     * Suspender inscrição
     */
    public Enrollment suspendEnrollment(String enrollmentId, String reason) {
        Enrollment enrollment = findById(enrollmentId);
        enrollment.setStatus(EnrollmentStatus.SUSPENDED);
        return enrollmentRepository.save(enrollment);
    }

    /**
     * Reativar inscrição
     */
    public Enrollment reactivateEnrollment(String enrollmentId) {
        Enrollment enrollment = findById(enrollmentId);
        
        if (enrollment.getStatus() == EnrollmentStatus.SUSPENDED) {
            enrollment.setStatus(EnrollmentStatus.ACTIVE);
        }
        
        return enrollmentRepository.save(enrollment);
    }

    /**
     * Cancelar inscrição
     */
    public void cancelEnrollment(String enrollmentId) {
        Enrollment enrollment = findById(enrollmentId);
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    // ===== RELATÓRIOS E ESTATÍSTICAS =====

    /**
     * Obter estatísticas de inscrições
     */
    public EnrollmentStats getEnrollmentStats() {
        long totalEnrollments = enrollmentRepository.count();
        long activeEnrollments = enrollmentRepository.countByStatus(EnrollmentStatus.ACTIVE);
        long completedEnrollments = enrollmentRepository.countCompletedEnrollments();
        long pendingPayments = enrollmentRepository.findPendingPayments().size();

        return new EnrollmentStats(totalEnrollments, activeEnrollments, completedEnrollments, pendingPayments);
    }

    /**
     * Contar inscrições de um curso
     */
    public long countEnrollmentsByCourse(String courseId) {
        return enrollmentRepository.countByCourseId(courseId);
    }

    /**
     * Contar inscrições ativas de um curso
     */
    public long countActiveEnrollmentsByCourse(String courseId) {
        return enrollmentRepository.countActiveByCourseId(courseId);
    }

    /**
     * Buscar estudantes inativos (sem acesso há X dias)
     */
    public List<Enrollment> findInactiveStudents(int daysInactive) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(daysInactive);
        return enrollmentRepository.findInactiveStudents(threshold);
    }

    /**
     * Buscar estudantes com baixo progresso
     */
    public List<Enrollment> findStudentsWithLowProgress(double maxProgress) {
        return enrollmentRepository.findActiveWithLowProgress(maxProgress);
    }

    /**
     * Buscar inscrições recentes (últimos X dias)
     */
    public List<Enrollment> findRecentEnrollments(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return enrollmentRepository.findRecentEnrollments(since);
    }

    // ===== CLASSES AUXILIARES =====

    /**
     * Estatísticas de inscrições
     */
    public static class EnrollmentStats {
        public final long totalEnrollments;
        public final long activeEnrollments;
        public final long completedEnrollments;
        public final long pendingPayments;

        public EnrollmentStats(long totalEnrollments, long activeEnrollments, 
                             long completedEnrollments, long pendingPayments) {
            this.totalEnrollments = totalEnrollments;
            this.activeEnrollments = activeEnrollments;
            this.completedEnrollments = completedEnrollments;
            this.pendingPayments = pendingPayments;
        }
    }
}