package com.chobbi.server.catalog.controller;

import com.chobbi.server.catalog.dto.CategoryDto;
import com.chobbi.server.catalog.services.CategoryServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServices categoryServices;

    @GetMapping(value = "/tree")
    public ResponseEntity<?> getTree() {
        return ResponseEntity.ok(categoryServices.getTree());
    }

    @GetMapping(value = "/tree/{categoryId}")
    public ResponseEntity<?> getTreeByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryServices.getTree(categoryId));
    }

    @GetMapping("/{categoryId}/branch")
    public ResponseEntity<CategoryDto> getBranchOfCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryServices.getBranchOfCategory(categoryId));
    }

    /** Danh sách tất cả category lá (không có con), dùng cho trang chủ. */
    @GetMapping("/leaves")
    public ResponseEntity<List<CategoryDto>> getLeafCategories() {
        return ResponseEntity.ok(categoryServices.getLeafCategories());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryServices.getById(categoryId));
    }

    @GetMapping("/{categoryId}/attributes")
    public ResponseEntity<?> getAttributeById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryServices.getAttributes(categoryId));
    }
}
