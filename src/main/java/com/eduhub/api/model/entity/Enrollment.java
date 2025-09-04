package com.eduhub.api.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Entity Enrollment - Representa uma inscrição de usuário em um curso
 * 
 * Equivale a uma tabela pivot no Laravel (user_course_enrollments)
 * Mas com funcionalidades adicionais como progresso e status
 */
@Document(collection = "enrollments")
@CompoundIndex(def = "{'student.id': 1, 'course.id': 1}", unique = true) // Evita inscrição duplicada
public class Enrollment {

    @Id
    private String id;

    // Informações do estudante (embedded)
    @NotNull(message = "Estudante é obrigatório")
    private StudentInfo student;

    // Informações do curso (embedded)
    @NotNull(message = "Curso é obrigatório")
    private CourseInfo course;

    // Status da inscrição
    @NotNull(message = "Status é obrigatório")
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    // Progresso do curso
    @Min(value = 0, message = "Progresso deve ser maior ou igual a 0")
    @Max(value = 100, message = "Progresso deve ser menor ou igual a 100")
    private double progressPercentage = 0.0;

    // Aulas completadas
    private List<CompletedLesson> completedLessons = new ArrayList<>();

    // Datas importantes
    @CreatedDate
    private LocalDateTime enrolledAt;

    private LocalDateTime completedAt; // Quando concluiu o curso

    private LocalDateTime lastAccessedAt; // Último acesso

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Informações de pagamento (se o curso for pago)
    private PaymentInfo payment;

    // Avaliação do curso pelo estudante
    private CourseRating rating;

    // Certificado
    private CertificateInfo certificate;

    // ===== CLASSES INTERNAS (Embedded Documents) =====

    /**
     * Informações do estudante (embedded)
     */
    public static class StudentInfo {
        private String id;
        private String name;
        private String email;

        public StudentInfo() {}

        public StudentInfo(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public StudentInfo(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
        }

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    /**
     * Informações do curso (embedded)
     */
    public static class CourseInfo {
        private String id;
        private String title;
        private String instructorName;
        private double price;
        private int totalLessons;

        public CourseInfo() {}

        public CourseInfo(String id, String title, String instructorName, double price, int totalLessons) {
            this.id = id;
            this.title = title;
            this.instructorName = instructorName;
            this.price = price;
            this.totalLessons = totalLessons;
        }

        public CourseInfo(Course course) {
            this.id = course.getId();
            this.title = course.getTitle();
            this.instructorName = course.getInstructor().getName();
            this.price = course.getPrice();
            this.totalLessons = course.getTotalLessons();
        }

        // Getters e Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getInstructorName() { return instructorName; }
        public void setInstructorName(String instructorName) { this.instructorName = instructorName; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getTotalLessons() { return totalLessons; }
        public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }
    }

    /**
     * Aula completada
     */
    public static class CompletedLesson {
        private String moduleTitle;
        private String lessonTitle;
        private int lessonOrder;
        private LocalDateTime completedAt;
        private int timeSpent; // tempo gasto em segundos

        public CompletedLesson() {}

        public CompletedLesson(String moduleTitle, String lessonTitle, int lessonOrder) {
            this.moduleTitle = moduleTitle;
            this.lessonTitle = lessonTitle;
            this.lessonOrder = lessonOrder;
            this.completedAt = LocalDateTime.now();
        }

        // Getters e Setters
        public String getModuleTitle() { return moduleTitle; }
        public void setModuleTitle(String moduleTitle) { this.moduleTitle = moduleTitle; }
        public String getLessonTitle() { return lessonTitle; }
        public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }
        public int getLessonOrder() { return lessonOrder; }
        public void setLessonOrder(int lessonOrder) { this.lessonOrder = lessonOrder; }
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        public int getTimeSpent() { return timeSpent; }
        public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }
    }

    /**
     * Informações de pagamento
     */
    public static class PaymentInfo {
        private double amountPaid;
        private String paymentMethod;
        private String transactionId;
        private LocalDateTime paidAt;
        private PaymentStatus status;

        public PaymentInfo() {}

        public PaymentInfo(double amountPaid, String paymentMethod, String transactionId) {
            this.amountPaid = amountPaid;
            this.paymentMethod = paymentMethod;
            this.transactionId = transactionId;
            this.paidAt = LocalDateTime.now();
            this.status = PaymentStatus.PAID;
        }

        // Getters e Setters
        public double getAmountPaid() { return amountPaid; }
        public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public LocalDateTime getPaidAt() { return paidAt; }
        public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
        public PaymentStatus getStatus() { return status; }
        public void setStatus(PaymentStatus status) { this.status = status; }
    }

    /**
     * Avaliação do curso
     */
    public static class CourseRating {
        @Min(1) @Max(5)
        private int rating; // 1 a 5 estrelas
        private String comment;
        private LocalDateTime ratedAt;

        public CourseRating() {}

        public CourseRating(int rating, String comment) {
            this.rating = rating;
            this.comment = comment;
            this.ratedAt = LocalDateTime.now();
        }

        // Getters e Setters
        public int getRating() { return rating; }
        public void setRating(int rating) { this.rating = rating; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public LocalDateTime getRatedAt() { return ratedAt; }
        public void setRatedAt(LocalDateTime ratedAt) { this.ratedAt = ratedAt; }
    }

    /**
     * Informações do certificado
     */
    public static class CertificateInfo {
        private String certificateId;
        private String certificateUrl;
        private LocalDateTime issuedAt;

        public CertificateInfo() {}

        public CertificateInfo(String certificateId, String certificateUrl) {
            this.certificateId = certificateId;
            this.certificateUrl = certificateUrl;
            this.issuedAt = LocalDateTime.now();
        }

        // Getters e Setters
        public String getCertificateId() { return certificateId; }
        public void setCertificateId(String certificateId) { this.certificateId = certificateId; }
        public String getCertificateUrl() { return certificateUrl; }
        public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }
        public LocalDateTime getIssuedAt() { return issuedAt; }
        public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }
    }

    // ===== CONSTRUTORES =====

    public Enrollment() {}

    public Enrollment(User student, Course course) {
        this.student = new StudentInfo(student);
        this.course = new CourseInfo(course);
        this.status = EnrollmentStatus.ACTIVE;
        this.progressPercentage = 0.0;
        this.lastAccessedAt = LocalDateTime.now();
        
        // Se o curso for gratuito, não precisa de pagamento
        if (course.getPrice() == 0.0) {
            this.payment = new PaymentInfo(0.0, "FREE", "FREE_COURSE");
        }
    }

    // ===== MÉTODOS UTILITÁRIOS =====

    /**
     * Marcar aula como completada
     */
    public void completeLesson(String moduleTitle, String lessonTitle, int lessonOrder) {
        CompletedLesson lesson = new CompletedLesson(moduleTitle, lessonTitle, lessonOrder);
        this.completedLessons.add(lesson);
        this.lastAccessedAt = LocalDateTime.now();
        updateProgress();
    }

    /**
     * Calcular e atualizar progresso
     */
    public void updateProgress() {
        if (course.getTotalLessons() > 0) {
            this.progressPercentage = (double) completedLessons.size() / course.getTotalLessons() * 100;
            
            // Se completou 100%, marcar como concluído
            if (this.progressPercentage >= 100.0) {
                this.status = EnrollmentStatus.COMPLETED;
                this.completedAt = LocalDateTime.now();
            }
        }
    }

    /**
     * Verificar se curso foi concluído
     */
    public boolean isCompleted() {
        return status == EnrollmentStatus.COMPLETED || progressPercentage >= 100.0;
    }

    /**
     * Verificar se aula específica foi completada
     */
    public boolean isLessonCompleted(String lessonTitle) {
        return completedLessons.stream()
                .anyMatch(lesson -> lesson.getLessonTitle().equals(lessonTitle));
    }

    /**
     * Obter total de aulas completadas
     */
    public int getTotalCompletedLessons() {
        return completedLessons.size();
    }

    /**
     * Avaliar curso
     */
    public void rateCourse(int rating, String comment) {
        this.rating = new CourseRating(rating, comment);
    }

    /**
     * Gerar certificado
     */
    public void generateCertificate(String certificateId, String certificateUrl) {
        if (isCompleted()) {
            this.certificate = new CertificateInfo(certificateId, certificateUrl);
        } else {
            throw new RuntimeException("Não é possível gerar certificado para curso não concluído");
        }
    }

    // ===== GETTERS E SETTERS =====

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public StudentInfo getStudent() { return student; }
    public void setStudent(StudentInfo student) { this.student = student; }

    public CourseInfo getCourse() { return course; }
    public void setCourse(CourseInfo course) { this.course = course; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(double progressPercentage) { this.progressPercentage = progressPercentage; }

    public List<CompletedLesson> getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(List<CompletedLesson> completedLessons) { this.completedLessons = completedLessons; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public PaymentInfo getPayment() { return payment; }
    public void setPayment(PaymentInfo payment) { this.payment = payment; }

    public CourseRating getRating() { return rating; }
    public void setRating(CourseRating rating) { this.rating = rating; }

    public CertificateInfo getCertificate() { return certificate; }
    public void setCertificate(CertificateInfo certificate) { this.certificate = certificate; }

    // ===== EQUALS, HASHCODE E TOSTRING =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Enrollment that = (Enrollment) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id='" + id + '\'' +
                ", student=" + student.getName() +
                ", course=" + course.getTitle() +
                ", status=" + status +
                ", progress=" + progressPercentage + "%" +
                ", enrolledAt=" + enrolledAt +
                '}';
    }
}