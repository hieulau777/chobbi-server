package com.chobbi.server.catalog.services;

import com.chobbi.server.catalog.dto.admin.AdminAttributeRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeResponse;
import com.chobbi.server.catalog.dto.admin.AdminAttributeSeedRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeValueRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeValueResponse;
import com.chobbi.server.catalog.dto.admin.AdminCategoryRequest;
import com.chobbi.server.catalog.dto.admin.AdminCategoryResponse;
import com.chobbi.server.catalog.dto.admin.AdminCategoryTreeSeedRequest;
import com.chobbi.server.catalog.dto.admin.AdminProductSeedRequest;

import java.util.List;

public interface AdminCatalogService {

    // Category
    List<AdminCategoryResponse> listCategories();

    /**
     * Lấy danh sách các category leaf (không có con, chưa xoá mềm).
     */
    List<AdminCategoryResponse> listLeafCategories();

    AdminCategoryResponse createCategory(AdminCategoryRequest request);

    AdminCategoryResponse updateCategory(Long id, AdminCategoryRequest request);

    void deleteCategory(Long id);

    /**
     * Seed category theo cấu trúc cây.
     * Nhận vào danh sách root categories (có thể lồng children).
     */
    List<AdminCategoryResponse> seedCategoryTree(List<AdminCategoryTreeSeedRequest> roots);

    // Attributes
    List<AdminAttributeResponse> listAttributesByCategory(Long categoryId);

    AdminAttributeResponse createAttribute(Long categoryId, AdminAttributeRequest request);

    AdminAttributeResponse updateAttribute(Long attributeId, AdminAttributeRequest request);

    void deleteAttribute(Long attributeId);

    /**
     * Seed attributes + attribute values cho một hoặc nhiều category leaf.
     * Hỗ trợ truyền theo categoryId hoặc categoryName.
     */
    List<AdminAttributeResponse> seedAttributesForCategory(List<AdminAttributeSeedRequest> requests);

    // Attribute values
    List<AdminAttributeValueResponse> listAttributeValues(Long attributeId);

    AdminAttributeValueResponse createAttributeValue(Long attributeId, AdminAttributeValueRequest request);

    AdminAttributeValueResponse updateAttributeValue(Long id, AdminAttributeValueRequest request);

    void deleteAttributeValue(Long id);

    /**
     * Seed sản phẩm (product + variations) cho admin, dựa trên email account + category leaf name.
     * Chưa dùng auth, chỉ dùng trong internal admin tool.
     */
    void seedProducts(AdminProductSeedRequest request);
}

