package com.chobbi.server.catalog.controller;

import com.chobbi.server.catalog.dto.admin.AdminAttributeRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeResponse;
import com.chobbi.server.catalog.dto.admin.AdminAttributeSeedRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeValueRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeValueResponse;
import com.chobbi.server.catalog.dto.admin.AdminCategoryRequest;
import com.chobbi.server.catalog.dto.admin.AdminCategoryResponse;
import com.chobbi.server.catalog.dto.admin.AdminCategoryTreeSeedRequest;
import com.chobbi.server.catalog.dto.admin.AdminProductSeedRequest;
import com.chobbi.server.catalog.services.AdminCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/catalog")
@RequiredArgsConstructor
public class AdminCatalogController {

    private final AdminCatalogService adminCatalogService;

    // ===== Category =====

    @GetMapping("/categories")
    public ResponseEntity<List<AdminCategoryResponse>> listCategories() {
        return ResponseEntity.ok(adminCatalogService.listCategories());
    }

    @GetMapping("/categories/leaf")
    public ResponseEntity<List<AdminCategoryResponse>> listLeafCategories() {
        return ResponseEntity.ok(adminCatalogService.listLeafCategories());
    }

    /**
     * Data seeder: seed category theo cấu trúc cây.
     * Nhận vào JSON là một mảng các root categories, mỗi phần tử có field:
     * - name: String
     * - children: List<AdminCategoryTreeSeedRequest> (tùy chọn, lồng nhiều cấp).
     */
    @PostMapping("/categories/seed-tree")
    public ResponseEntity<List<AdminCategoryResponse>> seedCategoryTree(
            @RequestBody List<AdminCategoryTreeSeedRequest> request
    ) {
        return ResponseEntity.ok(adminCatalogService.seedCategoryTree(request));
    }

    @PostMapping("/categories")
    public ResponseEntity<AdminCategoryResponse> createCategory(@RequestBody AdminCategoryRequest request) {
        return ResponseEntity.ok(adminCatalogService.createCategory(request));
    }

    /**
     * Data seeder cho attributes + attribute values cho một hoặc nhiều category leaf.
     *
     * Body là một mảng AdminAttributeSeedRequest.
     * Mỗi phần tử có thể truyền:
     * - categoryId: Long (ưu tiên)
     * - hoặc categoryName: String (leaf category name, case-insensitive)
     */
    @PostMapping("/categories/seed-attributes")
    public ResponseEntity<List<AdminAttributeResponse>> seedAttributesForCategory(
            @RequestBody List<AdminAttributeSeedRequest> request
    ) {
        return ResponseEntity.ok(adminCatalogService.seedAttributesForCategory(request));
    }

    /**
     * Seed sản phẩm (product + variations) cho admin.
     * Body:
     * {
     *   "email": "seller@example.com",
     *   "products": [
     *     {
     *       "categoryName": "Áo thun",
     *       "product": { ... ProductRequest payload ... }
     *     }
     *   ]
     * }
     *
     * Chưa cần auth, chỉ dùng cho nội bộ admin.
     */
    @PostMapping("/products/seed")
    public ResponseEntity<Void> seedProducts(
            @RequestBody AdminProductSeedRequest request
    ) {
        adminCatalogService.seedProducts(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<AdminCategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody AdminCategoryRequest request
    ) {
        return ResponseEntity.ok(adminCatalogService.updateCategory(id, request));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminCatalogService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Attributes =====

    @GetMapping("/categories/{categoryId}/attributes")
    public ResponseEntity<List<AdminAttributeResponse>> listAttributesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(adminCatalogService.listAttributesByCategory(categoryId));
    }

    @PostMapping("/categories/{categoryId}/attributes")
    public ResponseEntity<AdminAttributeResponse> createAttribute(
            @PathVariable Long categoryId,
            @RequestBody AdminAttributeRequest request
    ) {
        return ResponseEntity.ok(adminCatalogService.createAttribute(categoryId, request));
    }

    @PutMapping("/attributes/{attributeId}")
    public ResponseEntity<AdminAttributeResponse> updateAttribute(
            @PathVariable Long attributeId,
            @RequestBody AdminAttributeRequest request
    ) {
        return ResponseEntity.ok(adminCatalogService.updateAttribute(attributeId, request));
    }

    @DeleteMapping("/attributes/{attributeId}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long attributeId) {
        adminCatalogService.deleteAttribute(attributeId);
        return ResponseEntity.noContent().build();
    }

    // ===== Attribute values =====

    @GetMapping("/attributes/{attributeId}/values")
    public ResponseEntity<List<AdminAttributeValueResponse>> listAttributeValues(@PathVariable Long attributeId) {
        return ResponseEntity.ok(adminCatalogService.listAttributeValues(attributeId));
    }

    @PostMapping("/attributes/{attributeId}/values")
    public ResponseEntity<AdminAttributeValueResponse> createAttributeValue(
            @PathVariable Long attributeId,
            @RequestBody AdminAttributeValueRequest request
    ) {
        return ResponseEntity.ok(adminCatalogService.createAttributeValue(attributeId, request));
    }

    @PutMapping("/attribute-values/{id}")
    public ResponseEntity<AdminAttributeValueResponse> updateAttributeValue(
            @PathVariable Long id,
            @RequestBody AdminAttributeValueRequest request
    ) {
        return ResponseEntity.ok(adminCatalogService.updateAttributeValue(id, request));
    }

    @DeleteMapping("/attribute-values/{id}")
    public ResponseEntity<Void> deleteAttributeValue(@PathVariable Long id) {
        adminCatalogService.deleteAttributeValue(id);
        return ResponseEntity.noContent().build();
    }
}

