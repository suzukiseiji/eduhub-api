package com.eduhub.api.service;

import com.eduhub.api.model.entity.*;
import com.eduhub.api.repository.CourseRepository;
import com.eduhub.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * CourseService - Lógica de negócio para cursos
 * 
 * Equivale aos Services no Laravel
 * Implementa regras de negócio, validações e operações complexas
 */
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    // ===== CRUD BÁSICO =====

    /**
     * Criar novo curso
     * Equivale ao método create() de um Service no Laravel
     */
    public Course createCourse(String title, String description, CourseCategory category, 
                              CourseLevel level, String instructorId, double price) {
        
        // Validar se título já existe
        if (courseRepository.existsByTitleIgnoreCase(title)) {
            throw new RuntimeException("Já existe um curso com este título: " + title);
        }

        // Buscar instrutor
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instrutor não encontrado: " + instructorId));

        // Validar se usuário pode criar cursos
        if (!instructor.getProfile().canCreateCourses()) {
            throw new RuntimeException("Usuário não tem permissão para criar cursos");
        }

        // Criar curso
        Course course = new Course(title, description, category, level, instructor, price);
        
        return courseRepository.save(course);
    }

    /**
     * Buscar curso por ID
     * Equivale: Course::findOrFail($id)
     */
    public Course findById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado: " + id));
    }

    /**
     * Buscar curso por título
     */
    public Optional<Course> findByTitle(String title) {
        return courseRepository.findByTitleIgnoreCase(title);
    }

    /**
     * Listar todos os cursos
     * Equivale: Course::all()
     */
    public List<Course> findAllSimple() {
        return courseRepository.findAll();
    }

    /**
     * Listar cursos com paginação
     * Equivale: Course::paginate(10)
     */
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    /**
     * Atualizar curso
     */
    public Course updateCourse(String id, String title, String description, 
                              CourseCategory category, CourseLevel level, double price) {
        Course course = findById(id);

        // Verificar se título mudou e se já existe
        if (!course.getTitle().equalsIgnoreCase(title) && 
            courseRepository.existsByTitleIgnoreCase(title)) {
            throw new RuntimeException("Já existe um curso com este título: " + title);
        }

        // Atualizar campos
        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        course.setLevel(level);
        course.setPrice(price);

        return courseRepository.save(course);
    }

    /**
     * Deletar curso (soft delete)
     */
    public void deleteCourse(String id) {
        Course course = findById(id);
        course.setActive(false);
        courseRepository.save(course);
    }

    /**
     * Deletar curso permanentemente
     */
    public void deleteCoursePermanently(String id) {
        courseRepository.deleteById(id);
    }

    // ===== BUSCA POR FILTROS =====

    /**
     * Buscar cursos ativos
     * Equivale: Course::where('active', true)->get()
     */
    public List<Course> findActiveCourses() {
        return courseRepository.findByActive(true);
    }

    /**
     * Buscar cursos por categoria
     */
    public List<Course> findByCategory(CourseCategory category) {
        return courseRepository.findByCategoryAndActive(category, true);
    }

    /**
     * Buscar cursos por nível
     */
    public List<Course> findByLevel(CourseLevel level) {
        return courseRepository.findByLevel(level);
    }

    /**
     * Buscar cursos por instrutor
     */
    public List<Course> findByInstructor(String instructorId) {
        return courseRepository.findActiveByInstructorId(instructorId);
    }

    /**
     * Buscar cursos gratuitos
     */
    public List<Course> findFreeCourses() {
        return courseRepository.findFreeCourses();
    }

    /**
     * Buscar cursos premium (pagos)
     */
    public List<Course> findPremiumCourses() {
        return courseRepository.findPremiumCourses();
    }

    /**
     * Buscar cursos por faixa de preço
     */
    public List<Course> findByPriceRange(double minPrice, double maxPrice) {
        return courseRepository.findCoursesInPriceRange(minPrice, maxPrice);
    }

    // ===== BUSCA E PESQUISA =====

    /**
     * Pesquisar cursos por texto
     * Busca em título, descrição e nome do instrutor
     */
    public List<Course> searchCourses(String searchTerm) {
        return courseRepository.searchCourses(searchTerm);
    }

    /**
     * Filtro avançado de cursos
     */
    public List<Course> findCoursesWithFilters(CourseCategory category, CourseLevel level, 
                                              double maxPrice, String instructorId) {
        // Query customizada combinando filtros
        List<Course> courses = courseRepository.findByActive(true);
        
        // Aplicar filtros conforme necessário
        if (category != null) {
            courses = courses.stream()
                    .filter(course -> course.getCategory().equals(category))
                    .toList();
        }
        
        if (level != null) {
            courses = courses.stream()
                    .filter(course -> course.getLevel().equals(level))
                    .toList();
        }
        
        if (maxPrice > 0) {
            courses = courses.stream()
                    .filter(course -> course.getPrice() <= maxPrice)
                    .toList();
        }
        
        if (instructorId != null) {
            courses = courses.stream()
                    .filter(course -> course.getInstructor().getId().equals(instructorId))
                    .toList();
        }
        
        return courses;
    }

    // ===== GERENCIAMENTO DE MÓDULOS =====

    /**
     * Adicionar módulo ao curso
     */
    public Course addModule(String courseId, String moduleTitle, int order) {
        Course course = findById(courseId);
        
        Course.Module module = new Course.Module(moduleTitle, order);
        course.addModule(module);
        
        return courseRepository.save(course);
    }

    /**
     * Adicionar aula ao módulo
     */
    public Course addLessonToModule(String courseId, int moduleIndex, 
                                   String lessonTitle, int duration, int order, 
                                   LessonType type) {
        Course course = findById(courseId);
        
        if (moduleIndex >= course.getModules().size()) {
            throw new RuntimeException("Módulo não encontrado no índice: " + moduleIndex);
        }
        
        Course.Lesson lesson = new Course.Lesson(lessonTitle, duration, order, type);
        course.getModules().get(moduleIndex).addLesson(lesson);
        
        return courseRepository.save(course);
    }

    // ===== ESTATÍSTICAS =====

    /**
     * Obter estatísticas de cursos
     */
    public CourseStats getCourseStats() {
        long totalCourses = courseRepository.count();
        long activeCourses = courseRepository.countByActive(true);
        long freeCourses = courseRepository.countByActive(true); // Ajustar depois
        
        return new CourseStats(totalCourses, activeCourses, freeCourses);
    }

    /**
     * Contar cursos por categoria
     */
    public long countByCategory(CourseCategory category) {
        return courseRepository.countByCategory(category);
    }

    /**
     * Contar cursos por instrutor
     */
    public long countByInstructor(String instructorId) {
        return courseRepository.countByInstructorId(instructorId);
    }

    // ===== CONTROLE DE STATUS =====

    /**
     * Ativar/desativar curso
     */
    public Course toggleCourseStatus(String id) {
        Course course = findById(id);
        course.setActive(!course.isActive());
        return courseRepository.save(course);
    }

    /**
     * Verificar se instrutor pode gerenciar curso
     */
    public boolean canManageCourse(String courseId, String userId) {
        Course course = findById(courseId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Admin pode gerenciar tudo, instrutor só seus próprios cursos
        return user.isAdmin() || course.getInstructor().getId().equals(userId);
    }

    // Classe para estatísticas
    public static class CourseStats {
        public final long totalCourses;
        public final long activeCourses;
        public final long freeCourses;

        public CourseStats(long totalCourses, long activeCourses, long freeCourses) {
            this.totalCourses = totalCourses;
            this.activeCourses = activeCourses;
            this.freeCourses = freeCourses;
        }
    }
}