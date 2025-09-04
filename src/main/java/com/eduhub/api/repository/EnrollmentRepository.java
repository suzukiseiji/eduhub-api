package com.eduhub.api.repository;

import com.eduhub.api.model.entity.Enrollment;
import com.eduhub.api.model.entity.EnrollmentStatus;
import com.eduhub.api.model.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * EnrollmentRepository - Interface para acesso aos dados de inscrições
 * 
 * Métodos para gerenciar relacionamento entre usuários e cursos
 * Equivale às consultas de relacionamento many-to-many no Laravel
 */
@Repository
public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {

    // ===== BUSCA POR ESTUDANTE =====

    /**
     * Buscar todas as inscrições de um estudante
     * Equivale: $user->enrollments
     */
    @Query("{'student.id': ?0}")
    List<Enrollment> findByStudentId(String studentId);

    /**
     * Buscar inscrições ativas de um estudante
     * Equivale: $user->enrollments()->where('status', 'active')
     */
    @Query("{'student.id': ?0, 'status': ?1}")
    List<Enrollment> findByStudentIdAndStatus(String studentId, EnrollmentStatus status);

    /**
     * Buscar inscrições ativas de um estudante
     */
    @Query("{'student.id': ?0, 'status': 'ACTIVE'}")
    List<Enrollment> findActiveByStudentId(String studentId);

    /**
     * Buscar cursos concluídos por um estudante
     */
    @Query("{'student.id': ?0, 'status': 'COMPLETED'}")
    List<Enrollment> findCompletedByStudentId(String studentId);

    // ===== BUSCA POR CURSO =====

    /**
     * Buscar todas as inscrições de um curso
     * Equivale: $course->enrollments
     */
    @Query("{'course.id': ?0}")
    List<Enrollment> findByCourseId(String courseId);

    /**
     * Buscar inscrições ativas de um curso
     * Equivale: $course->enrollments()->where('status', 'active')
     */
    @Query("{'course.id': ?0, 'status': 'ACTIVE'}")
    List<Enrollment> findActiveByCourseId(String courseId);

    /**
     * Contar estudantes inscritos em um curso
     * Equivale: $course->enrollments()->count()
     */
    @Query(value = "{'course.id': ?0}", count = true)
    long countByCourseId(String courseId);

    /**
     * Contar estudantes ativos em um curso
     */
    @Query(value = "{'course.id': ?0, 'status': 'ACTIVE'}", count = true)
    long countActiveByCourseId(String courseId);

    // ===== VERIFICAÇÃO DE INSCRIÇÃO =====

    /**
     * Verificar se estudante está inscrito em um curso
     * Equivale: $user->courses()->where('course_id', $courseId)->exists()
     */
    @Query("{'student.id': ?0, 'course.id': ?1}")
    Optional<Enrollment> findByStudentIdAndCourseId(String studentId, String courseId);

    /**
     * Verificar se estudante tem inscrição ativa em um curso
     */
    @Query("{'student.id': ?0, 'course.id': ?1, 'status': 'ACTIVE'}")
    Optional<Enrollment> findActiveByStudentIdAndCourseId(String studentId, String courseId);

    /**
     * Verificar se inscrição existe (boolean)
     */
    @Query("{'student.id': ?0, 'course.id': ?1}")
    boolean existsByStudentIdAndCourseId(String studentId, String courseId);

    // ===== BUSCA POR STATUS =====

    /**
     * Buscar inscrições por status
     * Equivale: Enrollment::where('status', $status)
     */
    List<Enrollment> findByStatus(EnrollmentStatus status);

    /**
     * Buscar inscrições por status com paginação
     */
    Page<Enrollment> findByStatus(EnrollmentStatus status, Pageable pageable);

    /**
     * Buscar inscrições pendentes de pagamento
     */
    @Query("{'payment.status': 'PENDING'}")
    List<Enrollment> findPendingPayments();

    // ===== BUSCA POR PROGRESSO =====

    /**
     * Buscar estudantes que completaram um curso
     * Equivale: Enrollment::where('progress_percentage', 100)
     */
    @Query("{'progressPercentage': {'$gte': 100}}")
    List<Enrollment> findCompletedEnrollments();

    /**
     * Buscar estudantes com progresso mínimo
     */
    @Query("{'progressPercentage': {'$gte': ?0}}")
    List<Enrollment> findByProgressGreaterThanEqual(double minProgress);

    /**
     * Buscar estudantes com pouco progresso (menos de X%)
     */
    @Query("{'progressPercentage': {'$lt': ?0}, 'status': 'ACTIVE'}")
    List<Enrollment> findActiveWithLowProgress(double maxProgress);

    // ===== BUSCA POR DATA =====

    /**
     * Buscar inscrições criadas após uma data
     * Equivale: Enrollment::where('created_at', '>', $date)
     */
    List<Enrollment> findByEnrolledAtAfter(LocalDateTime date);

    /**
     * Buscar inscrições criadas entre datas
     * Equivale: Enrollment::whereBetween('created_at', [$start, $end])
     */
    List<Enrollment> findByEnrolledAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Buscar cursos concluídos após uma data
     */
    List<Enrollment> findByCompletedAtAfter(LocalDateTime date);

    /**
     * Buscar inscrições com último acesso recente
     */
    List<Enrollment> findByLastAccessedAtAfter(LocalDateTime date);

    // ===== BUSCA POR INSTRUTOR =====

    /**
     * Buscar inscrições dos cursos de um instrutor
     * Útil para dashboard do instrutor
     */
    @Query("{'course.instructorName': ?0}")
    List<Enrollment> findByCourseInstructorName(String instructorName);

    /**
     * Contar estudantes de todos os cursos de um instrutor
     */
    @Query(value = "{'course.instructorName': ?0}", count = true)
    long countByCourseInstructorName(String instructorName);

    // ===== QUERIES COMPLEXAS =====

    /**
     * Buscar top estudantes mais ativos (com mais cursos)
     * Agregação para ranking de estudantes
     */
    @Query("{'status': 'ACTIVE'}")
    List<Enrollment> findActiveEnrollments();

    /**
     * Buscar inscrições recentes (últimos 30 dias)
     */
    @Query("{'enrolledAt': {'$gte': ?0}}")
    List<Enrollment> findRecentEnrollments(LocalDateTime since);

    /**
     * Buscar estudantes inativos (sem acesso há X dias)
     */
    @Query("{'lastAccessedAt': {'$lt': ?0}, 'status': 'ACTIVE'}")
    List<Enrollment> findInactiveStudents(LocalDateTime sinceDate);

    /**
     * Buscar cursos mais populares (por número de inscrições)
     * Usado para análise de popularidade
     */
    @Query("{'status': {'$in': ['ACTIVE', 'COMPLETED']}}")
    List<Enrollment> findAllActiveOrCompleted();

    // ===== ESTATÍSTICAS E RELATÓRIOS =====

    /**
     * Contar inscrições por status
     */
    long countByStatus(EnrollmentStatus status);

    /**
     * Contar inscrições criadas após uma data
     */
    long countByEnrolledAtAfter(LocalDateTime date);

    /**
     * Contar cursos concluídos
     */
    @Query(value = "{'status': 'COMPLETED'}", count = true)
    long countCompletedEnrollments();

    /**
     * Buscar inscrições com avaliação
     */
    @Query("{'rating': {'$ne': null}}")
    List<Enrollment> findEnrollmentsWithRating();

    /**
     * Buscar inscrições com certificado emitido
     */
    @Query("{'certificate': {'$ne': null}}")
    List<Enrollment> findEnrollmentsWithCertificate();

    // ===== FILTROS AVANÇADOS =====

    /**
     * Buscar por faixa de progresso
     */
    @Query("{'progressPercentage': {'$gte': ?0, '$lte': ?1}}")
    List<Enrollment> findByProgressBetween(double minProgress, double maxProgress);

    /**
     * Buscar inscrições pagas (cursos premium)
     */
    @Query("{'course.price': {'$gt': 0}, 'payment.status': 'PAID'}")
    List<Enrollment> findPaidEnrollments();

    /**
     * Buscar inscrições gratuitas
     */
    @Query("{'course.price': 0}")
    List<Enrollment> findFreeEnrollments();

    /**
     * Buscar estudantes que precisam de atenção (baixo progresso + sem acesso há tempo)
     */
    @Query("{'progressPercentage': {'$lt': ?0}, 'lastAccessedAt': {'$lt': ?1}, 'status': 'ACTIVE'}")
    List<Enrollment> findStudentsNeedingAttention(double maxProgress, LocalDateTime lastAccessThreshold);

    // ===== ORDENAÇÃO =====

    /**
     * Buscar inscrições ordenadas por progresso (maior primeiro)
     */
    List<Enrollment> findByStatusOrderByProgressPercentageDesc(EnrollmentStatus status);

    /**
     * Buscar inscrições ordenadas por data de inscrição (mais recentes)
     */
    List<Enrollment> findByStatusOrderByEnrolledAtDesc(EnrollmentStatus status);

    /**
     * Buscar inscrições ordenadas por último acesso
     */
    List<Enrollment> findByStatusOrderByLastAccessedAtDesc(EnrollmentStatus status);
}