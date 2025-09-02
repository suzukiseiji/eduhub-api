package com.eduhub.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CourseController - Endpoints para testar nossa implementação no contexto de cursos
 */
@RestController
@RequestMapping("/courses")

public class CourseController {
    @Autowired
    private CourseService courseService;

    /**
     * GET /api/v1/users/test
     * Endpoint simples para testar se está funcionando
     */
    @GetMapping("/courses")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ EduHub API funcionando! MongoDB conectado!");
    }
}
