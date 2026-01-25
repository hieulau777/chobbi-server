package com.chobbi.server.catalog.services.impl;

import com.chobbi.server.catalog.dto.CategoryDto;
import com.chobbi.server.catalog.entity.CategoryEntity;
import com.chobbi.server.catalog.services.CategoryServices;
import com.chobbi.server.catalog.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryServicesImpl implements CategoryServices {
    private final CategoryRepo categoryRepo;

    @Override
    public List<CategoryDto> getTree() {
        List<CategoryEntity> allEntities = categoryRepo.findAllByDeletedAtIsNull();
        return buildTree(allEntities, null);
    }

    @Override
    public List<CategoryDto> getTree(Long categoryId) {
        // Sử dụng Native Query đệ quy đã viết trong Repo
        List<CategoryEntity> descendants = categoryRepo.findAllDescendants(categoryId);
        return buildTree(descendants, categoryId);
    }

    @Override
    public CategoryEntity getLeafCategoryOrThrow(Long categoryId) {
        // 1. Tìm Entity trước
        CategoryEntity category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        // 2. Check xem nó có phải là Leaf không
        boolean hasChildren = categoryRepo.existsByParentId(categoryId);

        if (hasChildren) {
            // Nếu có con -> Không phải leaf -> Throw lỗi
            throw new IllegalArgumentException("Category '" + category.getName() + "' is a parent category. Please select a leaf category.");
        }

        // 3. Đúng là Leaf thì trả về luôn để sử dụng
        return category;
    }

    /**
     * Hàm dùng chung để dựng cây từ danh sách phẳng.
     * @param entities Danh sách các node (toàn bộ hoặc một nhánh)
     * @param rootId ID của node gốc nếu đang lấy một nhánh (có thể null)
     */
    private List<CategoryDto> buildTree(List<CategoryEntity> entities, Long rootId) {
        if (entities.isEmpty()) return new ArrayList<>();

        Map<Long, CategoryDto> map = new HashMap<>();
        List<CategoryDto> roots = new ArrayList<>();

        // Bước 1: Khởi tạo Map DTO
        for (CategoryEntity entity : entities) {
            CategoryDto dto = new CategoryDto();
            dto.setId(entity.getId());
            dto.setName(entity.getName());
            dto.setChildren(new ArrayList<>());
            map.put(dto.getId(), dto);
        }

        // Bước 2: Dựng cây
        for (CategoryEntity entity : entities) {
            CategoryDto currentDto = map.get(entity.getId());
            CategoryEntity parentEntity = entity.getParent();

            // Kiểm tra xem node này có cha trong danh sách hiện tại không
            if (parentEntity == null || !map.containsKey(parentEntity.getId())) {
                // Nếu không có cha HOẶC cha nằm ngoài phạm vi list (trường hợp lấy 1 nhánh)
                // thì thằng này chính là Gốc của kết quả trả về
                roots.add(currentDto);
            } else {
                // Gắn vào danh sách children của cha
                CategoryDto parentDto = map.get(parentEntity.getId());
                parentDto.getChildren().add(currentDto);
            }
        }
        return roots;
    }
}
