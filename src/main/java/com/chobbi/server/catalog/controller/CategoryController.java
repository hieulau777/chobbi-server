package com.chobbi.server.catalog.controller;

import com.chobbi.server.catalog.dto.CategoryDto;
import com.chobbi.server.catalog.services.CategoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServices categoryServices;

    @GetMapping("/tree")
    public ResponseEntity<?> getTree() {
        return ResponseEntity.ok(categoryServices.getTree());
    }

    @GetMapping("/{categoryId}/attributes")
    public ResponseEntity<?> getAttributeById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryServices.getAttributes(categoryId));
    }
}
