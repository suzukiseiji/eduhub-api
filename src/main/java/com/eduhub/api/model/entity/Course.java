package com.eduhub.api.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity User - Representa um usuário do sistema
 */
@Document(collection = "courses")
public class Course {

    @Id
    private String id; // MongoDB usa String como ID por padrão

    @NotBlank(message = "Titulo é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String title;

    @Size(min = 2, max = 100, message = "Descrição deve ter entre 2 e 100 caracteres")
    private String description;

    private CourseCategory category;

    private CourseLevel level;

    private User instructor;

    private Array modules;

    private double price;

    private boolean active = true;
}
