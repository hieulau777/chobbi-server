package com.chobbi.server.catalog.services.impl;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
import com.chobbi.server.catalog.dto.admin.AdminAttributeRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeResponse;
import com.chobbi.server.catalog.dto.admin.AdminAttributeSeedRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeValueRequest;
import com.chobbi.server.catalog.dto.admin.AdminAttributeValueResponse;
import com.chobbi.server.catalog.dto.admin.AdminCategoryRequest;
import com.chobbi.server.catalog.dto.admin.AdminCategoryResponse;
import com.chobbi.server.catalog.dto.admin.AdminCategoryTreeSeedRequest;
import com.chobbi.server.catalog.dto.admin.AdminProductSeedRequest;
import com.chobbi.server.catalog.entity.AttributeValuesEntity;
import com.chobbi.server.catalog.entity.AttributesEntity;
import com.chobbi.server.catalog.entity.CategoryEntity;
import com.chobbi.server.catalog.entity.OptionsEntity;
import com.chobbi.server.catalog.entity.ProductAttributesEntity;
import com.chobbi.server.catalog.entity.ProductEntity;
import com.chobbi.server.catalog.entity.ProductImagesEntity;
import com.chobbi.server.catalog.entity.TierEntity;
import com.chobbi.server.catalog.entity.VariationEntity;
import com.chobbi.server.catalog.entity.VariationOptionEntity;
import com.chobbi.server.catalog.enums.StatusEnums;
import com.chobbi.server.catalog.repo.AttributeValuesRepo;
import com.chobbi.server.catalog.repo.AttributesRepo;
import com.chobbi.server.catalog.repo.CategoryRepo;
import com.chobbi.server.catalog.repo.ProductRepo;
import com.chobbi.server.catalog.services.AdminCatalogService;
import com.chobbi.server.catalog.services.ProductServices;
import com.chobbi.server.shop.entity.ShopEntity;
import com.chobbi.server.shop.repo.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminCatalogServiceImpl implements AdminCatalogService {

    private final CategoryRepo categoryRepo;
    private final AttributesRepo attributesRepo;
    private final AttributeValuesRepo attributeValuesRepo;
    private final AccountRepo accountRepo;
    private final ShopRepo shopRepo;
    private final ProductRepo productRepo;
    private final ProductServices productServices;

    // ===== Category =====

    @Override
    public List<AdminCategoryResponse> listCategories() {
        return categoryRepo.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminCategoryResponse> listLeafCategories() {
        return categoryRepo.findAllLeafCategories()
                .stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdminCategoryResponse> seedCategoryTree(List<AdminCategoryTreeSeedRequest> roots) {
        List<AdminCategoryResponse> created = new ArrayList<>();
        if (roots == null || roots.isEmpty()) {
            return created;
        }

        // Tập tên root category đã tồn tại trong DB (parent IS NULL, chưa xoá mềm)
        // để tránh trùng với seed mới.
        Set<String> existingRootNames = categoryRepo.findAllByDeletedAtIsNull()
                .stream()
                .filter(c -> c.getParent() == null)
                .map(CategoryEntity::getName)
                .filter(n -> n != null && !n.trim().isEmpty())
                .map(n -> n.trim().toLowerCase())
                .collect(Collectors.toSet());

        // Tập tên root được seed trong request này (để bắt trùng ngay trong JSON).
        Set<String> seededNames = new HashSet<>();

        for (AdminCategoryTreeSeedRequest root : roots) {
            created.addAll(createCategoryTreeRecursive(root, null, existingRootNames, seededNames));
        }

        return created;
    }

    private List<AdminCategoryResponse> createCategoryTreeRecursive(
            AdminCategoryTreeSeedRequest node,
            CategoryEntity parent,
            Set<String> existingNames,
            Set<String> seededNames
    ) {
        List<AdminCategoryResponse> created = new ArrayList<>();

        if (node.getName() == null || node.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required in seed tree.");
        }

        String name = node.getName().trim();
        String key = name.toLowerCase();

        // Chỉ enforce unique cho root category (parent == null)
        if (parent == null) {
            // Không cho phép trùng với root category đã tồn tại trong DB
            if (existingNames.contains(key)) {
                throw new IllegalArgumentException("Root category name already exists (in database): " + name);
            }

            // Không cho phép trùng root name trong chính payload seed
            if (seededNames.contains(key)) {
                throw new IllegalArgumentException("Duplicate root category name in seed payload: " + name);
            }

            seededNames.add(key);
        }

        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setParent(parent);

        CategoryEntity saved = categoryRepo.save(category);
        created.add(toCategoryResponse(saved));

        if (node.getChildren() != null) {
            for (AdminCategoryTreeSeedRequest child : node.getChildren()) {
                created.addAll(createCategoryTreeRecursive(child, saved, existingNames, seededNames));
            }
        }

        return created;
    }

    @Override
    public AdminCategoryResponse createCategory(AdminCategoryRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        String name = request.getName().trim();

        // Enforce unique theo (parent, name):
        // - Root: parentId == null, tên root không trùng với root khác
        // - Child: trong cùng một parent, tên con không trùng nhau

        CategoryEntity category = new CategoryEntity();
        category.setName(name);

        if (request.getParentId() != null) {
            CategoryEntity parent = categoryRepo.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with id: " + request.getParentId()));
            category.setParent(parent);
            if (categoryRepo.existsByNameIgnoreCaseAndParent_IdAndDeletedAtIsNull(name, parent.getId())) {
                throw new IllegalArgumentException("Category name already exists under this parent: " + name);
            }
        } else {
            category.setParent(null);
            if (categoryRepo.existsByNameIgnoreCaseAndParentIsNullAndDeletedAtIsNull(name)) {
                throw new IllegalArgumentException("Root category name already exists: " + name);
            }
        }

        CategoryEntity saved = categoryRepo.save(category);
        return toCategoryResponse(saved);
    }

    @Override
    public AdminCategoryResponse updateCategory(Long id, AdminCategoryRequest request) {
        CategoryEntity category = categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        String name = request.getName().trim();

        category.setName(name);

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be parent of itself.");
            }
            CategoryEntity parent = categoryRepo.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found with id: " + request.getParentId()));
            category.setParent(parent);
            if (categoryRepo.existsByNameIgnoreCaseAndParent_IdAndDeletedAtIsNullAndIdNot(name, parent.getId(), id)) {
                throw new IllegalArgumentException("Category name already exists under this parent: " + name);
            }
        } else {
            category.setParent(null);
            if (categoryRepo.existsByNameIgnoreCaseAndParentIsNullAndDeletedAtIsNullAndIdNot(name, id)) {
                throw new IllegalArgumentException("Root category name already exists: " + name);
            }
        }

        CategoryEntity saved = categoryRepo.save(category);
        return toCategoryResponse(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // Không cho xoá nếu đang có con trực tiếp
        boolean hasChildren = categoryRepo.existsByParentId(id);
        if (hasChildren) {
            throw new IllegalStateException("Cannot delete category that has children.");
        }

        category.softDelete();
        categoryRepo.save(category);
    }

    private AdminCategoryResponse toCategoryResponse(CategoryEntity entity) {
        Long parentId = entity.getParent() != null ? entity.getParent().getId() : null;
        return new AdminCategoryResponse(entity.getId(), entity.getName(), parentId);
    }

    // ===== Attributes =====

    @Override
    public List<AdminAttributeResponse> listAttributesByCategory(Long categoryId) {
        CategoryEntity category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        return category.getAttributes()
                .stream()
                .filter(attr -> !attr.isDeleted())
                .map(this::toAttributeResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminAttributeResponse createAttribute(Long categoryId, AdminAttributeRequest request) {
        CategoryEntity category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        // Chỉ cho tạo attribute trên leaf category
        boolean hasChildren = categoryRepo.existsByParentId(categoryId);
        if (hasChildren) {
            throw new IllegalArgumentException("Attributes can only be attached to leaf categories.");
        }

        AttributesEntity attr = new AttributesEntity();
        attr.setName(request.getName());
        attr.setIsRequired(request.getIsRequired());
        attr.setIsCustomAllow(request.getIsCustomAllow());
        attr.setIsMultipleAllow(request.getIsMultipleAllow());
        attr.setType(request.getType());
        attr.setCategoryEntity(category);

        AttributesEntity saved = attributesRepo.save(attr);
        return toAttributeResponse(saved);
    }

    @Override
    public List<AdminAttributeResponse> seedAttributesForCategory(List<AdminAttributeSeedRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        List<AdminAttributeResponse> allCreated = new ArrayList<>();

        for (AdminAttributeSeedRequest request : requests) {
            // Ưu tiên categoryId, nếu không có thì dùng categoryPath (root + leaf)
            Long categoryId = request.getCategoryId();
            CategoryEntity category;

            if (categoryId != null) {
                category = categoryRepo.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
            } else if (request.getCategoryPath() != null) {
                String rootName = request.getCategoryPath().getRootName();
                String leafName = request.getCategoryPath().getLeafName();

                if (rootName == null || rootName.trim().isEmpty()
                        || leafName == null || leafName.trim().isEmpty()) {
                    throw new IllegalArgumentException("categoryPath.rootName and leafName are required");
                }

                String root = rootName.trim();
                String leaf = leafName.trim();

                List<CategoryEntity> all = categoryRepo.findAllByDeletedAtIsNull();

                // Tìm leaf theo tên leaf + ancestor root có tên root
                category = all.stream()
                        .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(leaf))
                        .filter(this::isLeafCategory)
                        .filter(c -> hasRootAncestorNamed(c, root))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Leaf category not found with root='" + root + "', leaf='" + leaf + "'"));
            } else {
                throw new IllegalArgumentException("Either categoryId or categoryPath is required");
            }

            Long effectiveCategoryId = category.getId();

            // Validate category là leaf
            boolean hasChildren = categoryRepo.existsByParentId(effectiveCategoryId);
            if (hasChildren) {
                throw new IllegalArgumentException("Attributes can only be attached to leaf categories. Category id=" + effectiveCategoryId);
            }

            if (request.getAttributes() == null || request.getAttributes().isEmpty()) {
                continue;
            }

            for (AdminAttributeSeedRequest.AttributeWithValues attrSeed : request.getAttributes()) {
                AttributesEntity attr = new AttributesEntity();
                attr.setName(attrSeed.getName());
                attr.setIsRequired(attrSeed.getIsRequired());
                attr.setIsCustomAllow(attrSeed.getIsCustomAllow());
                attr.setIsMultipleAllow(attrSeed.getIsMultipleAllow());
                attr.setType(attrSeed.getType());
                attr.setCategoryEntity(category);

                AttributesEntity savedAttr = attributesRepo.save(attr);
                allCreated.add(toAttributeResponse(savedAttr));

                if (attrSeed.getValues() != null) {
                    for (AdminAttributeSeedRequest.AttributeValueSeed valSeed : attrSeed.getValues()) {
                        AttributeValuesEntity value = new AttributeValuesEntity();
                        value.setIsCustom(valSeed.getIsCustom());
                        value.setValueText(valSeed.getValueText());
                        value.setValueNumber(valSeed.getValueNumber());
                        value.setValueBoolean(valSeed.getValueBoolean());
                        // valueDate ở DTO seed dạng String (ISO), nên không set ở đây,
                        // seeder có thể dùng TEXT/NUMBER/BOOLEAN là chủ yếu.
                        value.setAttributesEntity(savedAttr);

                        attributeValuesRepo.save(value);
                    }
                }
            }
        }

        return allCreated;
    }

    @Override
    public void seedProducts(AdminProductSeedRequest request) {
        if (request == null || request.getProducts() == null || request.getProducts().isEmpty()) {
            return;
        }
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("email is required for product seeding");
        }
        AccountEntity account = accountRepo.findByEmail(email.trim())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for email: " + email));
        ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(account.getId())
                .orElseThrow(() -> new IllegalArgumentException("Shop not found for account id: " + account.getId()));
        List<CategoryEntity> allCategories = categoryRepo.findAllByDeletedAtIsNull();
        for (AdminProductSeedRequest.ProductSeedItem item : request.getProducts()) {
            if (item == null) continue;
            // 1. Resolve category leaf từ categoryPath (root + leaf)
            AdminProductSeedRequest.CategoryPath cp = item.getCategoryPath();
            if (cp == null
                    || cp.getRootName() == null || cp.getRootName().trim().isEmpty()
                    || cp.getLeafName() == null || cp.getLeafName().trim().isEmpty()) {
                continue;
            }
            String root = cp.getRootName().trim();
            String leaf = cp.getLeafName().trim();
            CategoryEntity category = allCategories.stream()
                    .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(leaf))
                    .filter(this::isLeafCategory)
                    .filter(c -> hasRootAncestorNamed(c, root))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Leaf category not found for product with root='" + root + "', leaf='" + leaf + "'"));
            // 2. Validate name, tránh trùng sản phẩm trong cùng shop
            String name = item.getName() != null ? item.getName().trim() : "";
            if (name.isEmpty()) continue;
            boolean exists = productRepo.findByNameAndShopEntity_IdAndDeletedAtIsNull(name, shop.getId()).isPresent();
            if (exists) continue;
            // 3. Tạo ProductEntity base
            ProductEntity product = new ProductEntity();
            product.setName(name);
            product.setDescription(item.getDescription());
            product.setWeight(item.getWeight() != null ? item.getWeight() : 0L);
            product.setShopEntity(shop);
            product.setCategoryEntity(category);
            product.setStatus(StatusEnums.DRAFT); // luôn DRAFT như anh yêu cầu
            // 4. Thumbnail placeholder
            ProductImagesEntity thumb = new ProductImagesEntity();
            thumb.setProductEntity(product);
            thumb.setPath("/static/images/placeholder-ao-thun.jpg");
            thumb.setSortOrder(1);
            product.getProductImages().add(thumb);
            product.setThumbnail(thumb.getPath());
            // 5. Thuộc tính cấp product (Thương hiệu, v.v.)
            if (item.getAttributes() != null && !item.getAttributes().isEmpty()) {
                Map<String, AttributesEntity> attrsByName = loadAttributesByName(category);
                List<ProductAttributesEntity> productAttributes = new ArrayList<>();
                for (AdminProductSeedRequest.SeedAttribute attrSeed : item.getAttributes()) {
                    if (attrSeed == null
                            || attrSeed.getAttributeName() == null
                            || attrSeed.getValueName() == null) {
                        continue;
                    }
                    AttributesEntity attrEntity = attrsByName.get(attrSeed.getAttributeName().trim());
                    if (attrEntity == null) continue;
                    AttributeValuesEntity valueEntity = findAttributeValue(attrEntity, attrSeed.getValueName().trim())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Attribute value not found: " + attrSeed.getValueName()
                                            + " for attribute: " + attrSeed.getAttributeName()));
                    ProductAttributesEntity pa = new ProductAttributesEntity();
                    pa.setProductEntity(product);
                    pa.setAttributesEntity(attrEntity);
                    pa.setAttributeValuesEntity(valueEntity);
                    productAttributes.add(pa);
                }
                product.setProductAttributes(productAttributes);
            }
            // 6. Tiers + variations
            List<AdminProductSeedRequest.SeedTier> tiers = item.getTiers();
            List<AdminProductSeedRequest.SeedVariation> variationsSeed = item.getVariations() != null
                    ? item.getVariations()
                    : List.of();
            Map<String, OptionsEntity> optionLookup = new HashMap<>();
            if (tiers != null && !tiers.isEmpty()) {
                // Multi-variation
                for (AdminProductSeedRequest.SeedTier tierSeed : tiers) {
                    if (tierSeed == null || tierSeed.getName() == null) continue;
                    String tierName = tierSeed.getName().trim();
                    if (tierName.isEmpty()) continue;
                    TierEntity tier = new TierEntity();
                    tier.setProductEntity(product);
                    tier.setName(tierName);
                    tier.setHasImages(Boolean.TRUE.equals(tierSeed.getHasImages()));
                    product.getTiers().add(tier);
                    if (tierSeed.getOptions() != null) {
                        for (String optNameRaw : tierSeed.getOptions()) {
                            if (optNameRaw == null) continue;
                            String optName = optNameRaw.trim();
                            if (optName.isEmpty()) continue;
                            OptionsEntity opt = new OptionsEntity();
                            opt.setTierEntity(tier);
                            opt.setName(optName);
                            // option images placeholder nếu tier.hasImages = true
                            if (Boolean.TRUE.equals(tierSeed.getHasImages())) {
                                opt.setImgPath("/static/images/placeholder-ao-thun.jpg");
                            }
                            tier.getOptions().add(opt);
                            String key = tierName.toLowerCase() + ":" + optName.toLowerCase();
                            optionLookup.put(key, opt);
                        }
                    }
                }
            }
            // 7. Variations
            List<VariationEntity> variations = new ArrayList<>();
            if (tiers == null || tiers.isEmpty()) {
                // Single-variation: lấy variation đầu tiên
                if (!variationsSeed.isEmpty()) {
                    AdminProductSeedRequest.SeedVariation sv = variationsSeed.getFirst();
                    if (sv.getPrice() != null) {
                        VariationEntity v = new VariationEntity();
                        v.setProductEntity(product);
                        v.setPrice(sv.getPrice());
                        v.setPriceDiscount(
                                sv.getDiscountPrice() != null ? sv.getDiscountPrice() : sv.getPrice()
                        );
                        v.setStock(sv.getStock() != null ? sv.getStock() : 0);
                        variations.add(v);
                    }
                }
            } else {
                // Multi-variation
                for (AdminProductSeedRequest.SeedVariation sv : variationsSeed) {
                    if (sv == null || sv.getPrice() == null) continue;
                    VariationEntity v = new VariationEntity();
                    v.setProductEntity(product);
                    v.setPrice(sv.getPrice());
                    v.setPriceDiscount(
                            sv.getDiscountPrice() != null ? sv.getDiscountPrice() : sv.getPrice()
                    );
                    v.setStock(sv.getStock() != null ? sv.getStock() : 0);
                    if (sv.getOptions() != null) {
                        for (AdminProductSeedRequest.SeedOptionRef ref : sv.getOptions()) {
                            if (ref == null
                                    || ref.getTierName() == null
                                    || ref.getOptionName() == null) {
                                continue;
                            }
                            String key = ref.getTierName().trim().toLowerCase()
                                    + ":" + ref.getOptionName().trim().toLowerCase();
                            OptionsEntity opt = optionLookup.get(key);
                            if (opt == null) continue;
                            VariationOptionEntity vo = new VariationOptionEntity();
                            vo.setVariationEntity(v);
                            vo.setOptionsEntity(opt);
                            v.getVariationOptions().add(vo);
                        }
                    }
                    variations.add(v);
                }
            }
            product.setVariations(variations);
            // 8. Lưu product
            productRepo.save(product);
        }
    }

    @Override
    public AdminAttributeResponse updateAttribute(Long attributeId, AdminAttributeRequest request) {
        AttributesEntity attr = attributesRepo.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attributeId));

        attr.setName(request.getName());
        attr.setIsRequired(request.getIsRequired());
        attr.setIsCustomAllow(request.getIsCustomAllow());
        attr.setIsMultipleAllow(request.getIsMultipleAllow());
        attr.setType(request.getType());

        AttributesEntity saved = attributesRepo.save(attr);
        return toAttributeResponse(saved);
    }

    @Override
    public void deleteAttribute(Long attributeId) {
        AttributesEntity attr = attributesRepo.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attributeId));
        attr.softDelete();
        attributesRepo.save(attr);
    }

    private AdminAttributeResponse toAttributeResponse(AttributesEntity entity) {
        Long categoryId = entity.getCategoryEntity() != null ? entity.getCategoryEntity().getId() : null;
        return new AdminAttributeResponse(
                entity.getId(),
                categoryId,
                entity.getName(),
                entity.getIsRequired(),
                entity.getIsCustomAllow(),
                entity.getIsMultipleAllow(),
                entity.getType()
        );
    }

    // ===== Attribute values =====

    @Override
    public List<AdminAttributeValueResponse> listAttributeValues(Long attributeId) {
        AttributesEntity attr = attributesRepo.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attributeId));

        return attr.getAttributeValues()
                .stream()
                .filter(val -> !val.isDeleted())
                .map(this::toAttributeValueResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AdminAttributeValueResponse createAttributeValue(Long attributeId, AdminAttributeValueRequest request) {
        AttributesEntity attr = attributesRepo.findById(attributeId)
                .orElseThrow(() -> new IllegalArgumentException("Attribute not found with id: " + attributeId));

        AttributeValuesEntity value = new AttributeValuesEntity();
        value.setIsCustom(request.getIsCustom());
        value.setValueText(request.getValueText());
        value.setValueNumber(request.getValueNumber());
        value.setValueBoolean(request.getValueBoolean());
        value.setValueDate(request.getValueDate());
        value.setAttributesEntity(attr);

        AttributeValuesEntity saved = attributeValuesRepo.save(value);
        return toAttributeValueResponse(saved);
    }

    @Override
    public AdminAttributeValueResponse updateAttributeValue(Long id, AdminAttributeValueRequest request) {
        AttributeValuesEntity value = attributeValuesRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attribute value not found with id: " + id));

        value.setIsCustom(request.getIsCustom());
        value.setValueText(request.getValueText());
        value.setValueNumber(request.getValueNumber());
        value.setValueBoolean(request.getValueBoolean());
        value.setValueDate(request.getValueDate());

        AttributeValuesEntity saved = attributeValuesRepo.save(value);
        return toAttributeValueResponse(saved);
    }

    @Override
    public void deleteAttributeValue(Long id) {
        AttributeValuesEntity value = attributeValuesRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attribute value not found with id: " + id));
        value.softDelete();
        attributeValuesRepo.save(value);
    }

    private AdminAttributeValueResponse toAttributeValueResponse(AttributeValuesEntity entity) {
        Long attributeId = entity.getAttributesEntity() != null ? entity.getAttributesEntity().getId() : null;
        return new AdminAttributeValueResponse(
                entity.getId(),
                attributeId,
                entity.getIsCustom(),
                entity.getValueText(),
                entity.getValueNumber(),
                entity.getValueBoolean(),
                entity.getValueDate()
        );
    }

    private boolean isLeafCategory(CategoryEntity c) {
        return !categoryRepo.existsByParentId(c.getId());
    }

    private boolean hasRootAncestorNamed(CategoryEntity leaf, String rootName) {
        CategoryEntity current = leaf;
        CategoryEntity root = null;
        while (current != null) {
            root = current;
            current = current.getParent();
        }
        return root != null
                && root.getName() != null
                && root.getName().trim().equalsIgnoreCase(rootName.trim());
    }

    /**
     * So khớp đường dẫn category theo tên: ["Thời trang nam", "Áo", "Áo thun"].
     * Bắt đầu từ leaf đi ngược lên parent và so sánh từng phần tử.
     */
    private boolean matchesCategoryPath(CategoryEntity leaf, List<String> path) {
        int index = path.size() - 1;
        CategoryEntity current = leaf;

        while (current != null && index >= 0) {
            String expected = path.get(index);
            String actual = current.getName();
            if (!Objects.equals(
                    actual != null ? actual.trim() : null,
                    expected != null ? expected.trim() : null
            )) {
                return false;
            }
            current = current.getParent();
            index--;
        }

        return index < 0;
    }

    private Map<String, AttributesEntity> loadAttributesByName(CategoryEntity category) {
        Map<String, AttributesEntity> result = new HashMap<>();
        List<AttributesEntity> all = attributesRepo.findAll();
        for (AttributesEntity attr : all) {
            if (attr.getCategoryEntity() != null
                    && Objects.equals(attr.getCategoryEntity().getId(), category.getId())
                    && attr.getName() != null) {
                result.put(attr.getName().trim(), attr);
            }
        }
        return result;
    }

    private java.util.Optional<AttributeValuesEntity> findAttributeValue(AttributesEntity attribute, String valueText) {
        if (attribute.getAttributeValues() == null) {
            return java.util.Optional.empty();
        }
        return attribute.getAttributeValues()
                .stream()
                .filter(v -> v.getValueText() != null && v.getValueText().trim().equals(valueText))
                .findFirst();
    }
}

