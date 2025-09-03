package com.eduhub.api.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Entity Course - Representa um curso do sistema
 *
 * Baseado no JSON que você mostrou:
 * - Estrutura aninhada com módulos e aulas
 * - Informações do instrutor embedadas
 * - Campos de controle (preço, status, etc.)
 */
@Document(collection = "courses")
public class Course {

    @Id
    private String id;

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 2, max = 200, message = "Título deve ter entre 2 e 200 caracteres")
    @Indexed // Para busca rápida por título
    private String title;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 10, max = 1000, message = "Descrição deve ter entre 10 e 1000 caracteres")
    private String description;

    @NotNull(message = "Categoria é obrigatória")
    private CourseCategory category;

    @NotNull(message = "Nível é obrigatório")
    private CourseLevel level;

    // Informações do instrutor (embedded - como no JSON)
    @NotNull(message = "Instrutor é obrigatório")
    private InstructorInfo instructor;

    // Lista de módulos (embedded - como no JSON)
    private List<Module> modules = new ArrayList<>();

    @DecimalMin(value = "0.0", message = "Preço deve ser maior ou igual a zero")
    private double price;

    private boolean active = true;

    // Campos de auditoria
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Campos adicionais
    private String imageUrl; // Imagem do curso
    private int totalDuration; // Duração total em segundos
    private int totalLessons; // Total de aulas
    private String requirements; // Pré-requisitos
    private String whatYouWillLearn; // O que o aluno vai aprender

    // ===== CLASSES INTERNAS (Embedded Documents) =====

    /**
     * Informações do instrutor (embedded)
     * Baseado no JSON: "instructor": { "id": "...", "name": "...", "email": "..." }
     */
    public static class InstructorInfo {
        private String id;
        private String name;
        private String email;

        public InstructorInfo() {}

        public InstructorInfo(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Construtor a partir de User
        public InstructorInfo(User user) {
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
     * Módulo do curso (embedded)
     * Baseado no JSON: "modules": [{ "title": "...", "order": 1, "lessons": [...] }]
     */
    public static class Module {
        private String title;
        private int order;
        private List<Lesson> lessons = new ArrayList<>();

        public Module() {}

        public Module(String title, int order) {
            this.title = title;
            this.order = order;
        }

        // Getters e Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
        public List<Lesson> getLessons() { return lessons; }
        public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }

        // Método utilitário
        public void addLesson(Lesson lesson) {
            this.lessons.add(lesson);
        }
    }

    /**
     * Aula do curso (embedded)
     * Baseado no JSON: "lessons": [{ "title": "...", "duration": 1800, "order": 1, "type": "VIDEO" }]
     */
    public static class Lesson {
        private String title;
        private int duration; // em segundos
        private int order;
        private LessonType type;
        private String videoUrl;
        private String content; // Conteúdo textual
        private boolean completed = false;

        public Lesson() {}

        public Lesson(String title, int duration, int order, LessonType type) {
            this.title = title;
            this.duration = duration;
            this.order = order;
            this.type = type;
        }

        // Getters e Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
        public LessonType getType() { return type; }
        public void setType(LessonType type) { this.type = type; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    // ===== CONSTRUTORES =====

    public Course() {}

    public Course(String title, String description, CourseCategory category,
                  CourseLevel level, User instructor, double price) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.level = level;
        this.instructor = new InstructorInfo(instructor);
        this.price = price;
    }

    // ===== MÉTODOS UTILITÁRIOS =====

    /**
     * Adicionar módulo ao curso
     */
    public void addModule(Module module) {
        this.modules.add(module);
        updateTotalLessons();
        updateTotalDuration();
    }

    /**
     * Calcular total de aulas
     */
    private void updateTotalLessons() {
        this.totalLessons = modules.stream()
                .mapToInt(module -> module.getLessons().size())
                .sum();
    }

    /**
     * Calcular duração total
     */
    private void updateTotalDuration() {
        this.totalDuration = modules.stream()
                .flatMap(module -> module.getLessons().stream())
                .mapToInt(Lesson::getDuration)
                .sum();
    }

    /**
     * Verificar se curso tem módulos
     */
    public boolean hasModules() {
        return modules != null && !modules.isEmpty();
    }

    /**
     * Obter total de módulos
     */
    public int getTotalModules() {
        return modules != null ? modules.size() : 0;
    }

    // ===== GETTERS E SETTERS =====

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public CourseCategory getCategory() { return category; }
    public void setCategory(CourseCategory category) { this.category = category; }

    public CourseLevel getLevel() { return level; }
    public void setLevel(CourseLevel level) { this.level = level; }

    public InstructorInfo getInstructor() { return instructor; }
    public void setInstructor(InstructorInfo instructor) { this.instructor = instructor; }

    public List<Module> getModules() { return modules; }
    public void setModules(List<Module> modules) {
        this.modules = modules;
        updateTotalLessons();
        updateTotalDuration();
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getTotalDuration() { return totalDuration; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }

    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getWhatYouWillLearn() { return whatYouWillLearn; }
    public void setWhatYouWillLearn(String whatYouWillLearn) { this.whatYouWillLearn = whatYouWillLearn; }

    // ===== EQUALS, HASHCODE E TOSTRING =====

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(id, course.id) && Objects.equals(title, course.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", category=" + category +
                ", level=" + level +
                ", instructor=" + instructor.getName() +
                ", modulesCount=" + getTotalModules() +
                ", active=" + active +
                '}';
    }
}