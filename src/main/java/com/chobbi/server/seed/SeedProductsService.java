package com.chobbi.server.seed;

import com.chobbi.server.account.entity.AccountEntity;
import com.chobbi.server.account.repo.AccountRepo;
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
import com.chobbi.server.shop.entity.ShopEntity;
import com.chobbi.server.shop.repo.ShopRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeedProductsService {

    private static final String ACCOUNT_EMAIL = "hieulau0112@gmail.com";

    private final ObjectMapper objectMapper;
    private final AccountRepo accountRepo;
    private final ShopRepo shopRepo;
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final AttributesRepo attributesRepo;
    private final AttributeValuesRepo attributeValuesRepo;

    @Transactional
    public void seedFromJson(Path jsonPath) throws IOException {
        try (Reader reader = Files.newBufferedReader(jsonPath)) {
            SeedConfig config = objectMapper.readValue(reader, SeedConfig.class);

            AccountEntity account = accountRepo.findByEmail(ACCOUNT_EMAIL)
                    .orElseThrow(() -> new IllegalStateException("Account not found for email: " + ACCOUNT_EMAIL));

            ShopEntity shop = shopRepo.findByAccountEntity_IdAndDeletedAtIsNull(account.getId())
                    .orElseThrow(() -> new IllegalStateException("Shop not found for account id: " + account.getId()));

            CategoryEntity category = resolveCategory(config.getCategoryRef());

            Map<String, AttributesEntity> attributesByName = loadAttributesByName(category);

            for (SeedProduct productSeed : config.getProducts()) {
                String name = Objects.requireNonNullElse(productSeed.getName(), "").trim();
                if (name.isEmpty()) {
                    continue;
                }

                Optional<ProductEntity> existing =
                        productRepo.findByNameAndShopEntity_IdAndDeletedAtIsNull(name, shop.getId());
                if (existing.isPresent()) {
                    continue;
                }

                ProductEntity product = new ProductEntity();
                product.setName(name);
                product.setDescription(productSeed.getDescription());
                product.setThumbnail(config.getThumbnailPlaceholder());
                product.setWeight(
                        productSeed.getWeight() != null ? productSeed.getWeight() : 0L
                );
                product.setShopEntity(shop);
                product.setCategoryEntity(category);
                product.setStatus(StatusEnums.ACTIVE);

                List<ProductAttributesEntity> productAttributes = new ArrayList<>();
                if (productSeed.getAttributes() != null) {
                    for (SeedAttribute attrSeed : productSeed.getAttributes()) {
                        if (attrSeed == null
                                || attrSeed.getAttributeName() == null
                                || attrSeed.getValueName() == null) {
                            continue;
                        }
                        AttributesEntity attrEntity =
                                attributesByName.get(attrSeed.getAttributeName().trim());
                        if (attrEntity == null) {
                            continue;
                        }
                        AttributeValuesEntity valueEntity =
                                findAttributeValue(attrEntity, attrSeed.getValueName().trim())
                                        .orElseThrow(() -> new IllegalStateException(
                                                "Attribute value not found: " + attrSeed.getValueName()
                                                        + " for attribute: " + attrSeed.getAttributeName()));

                        ProductAttributesEntity pa = new ProductAttributesEntity();
                        pa.setProductEntity(product);
                        pa.setAttributesEntity(attrEntity);
                        pa.setAttributeValuesEntity(valueEntity);
                        productAttributes.add(pa);
                    }
                }
                product.setProductAttributes(productAttributes);

                if (config.getThumbnailPlaceholder() != null && !config.getThumbnailPlaceholder().isBlank()) {
                    ProductImagesEntity image = new ProductImagesEntity();
                    image.setProductEntity(product);
                    image.setPath(config.getThumbnailPlaceholder());
                    image.setSortOrder(0);
                    product.getProductImages().add(image);
                }

                List<VariationEntity> variations = new ArrayList<>();
                String type = productSeed.getType() != null ? productSeed.getType().trim().toLowerCase(Locale.ROOT) : "single";

                // Map tierName(lower) -> (optionName(lower) -> OptionsEntity)
                Map<String, Map<String, OptionsEntity>> tierOptionLookup = new HashMap<>();

                if ("multi".equals(type) && productSeed.getAttributesForVariations() != null) {
                    boolean hasImagesAssigned = false;
                    for (SeedAttributeForVariation attrTier : productSeed.getAttributesForVariations()) {
                        if (attrTier == null
                                || attrTier.getAttributeName() == null
                                || attrTier.getValues() == null
                                || attrTier.getValues().isEmpty()) {
                            continue;
                        }

                        String rawTierName = attrTier.getAttributeName().trim();
                        if (rawTierName.isEmpty()) {
                            continue;
                        }
                        String normTierKey = rawTierName.toLowerCase(Locale.ROOT);

                        TierEntity tier = new TierEntity();
                        tier.setProductEntity(product);
                        tier.setName(rawTierName);
                        // Đảm bảo đúng rule: chỉ 1 tier có hình ảnh (ở đây không seed ảnh option, nhưng vẫn đánh dấu 1 tier)
                        tier.setHasImages(!hasImagesAssigned);
                        hasImagesAssigned = true;
                        product.getTiers().add(tier);

                        Map<String, OptionsEntity> optionMap = new HashMap<>();
                        for (String value : attrTier.getValues()) {
                            if (value == null) continue;
                            String rawOptName = value.trim();
                            if (rawOptName.isEmpty()) continue;
                            String normOptKey = rawOptName.toLowerCase(Locale.ROOT);

                            OptionsEntity opt = new OptionsEntity();
                            opt.setTierEntity(tier);
                            opt.setName(rawOptName);
                            tier.getOptions().add(opt);
                            optionMap.put(normOptKey, opt);
                        }

                        if (!optionMap.isEmpty()) {
                            tierOptionLookup.put(normTierKey, optionMap);
                        }
                    }
                }

                if ("single".equals(type)) {
                    SeedPricing pricing = productSeed.getPricing();
                    if (pricing != null && pricing.getPrice() != null) {
                        VariationEntity v = new VariationEntity();
                        v.setProductEntity(product);
                        v.setPrice(pricing.getPrice());
                        v.setPriceDiscount(
                                pricing.getDiscountPrice() != null ? pricing.getDiscountPrice() : pricing.getPrice()
                        );
                        v.setStock(
                                pricing.getStock() != null ? pricing.getStock() : 0
                        );
                        variations.add(v);
                    }
                } else if ("multi".equals(type)) {
                    if (productSeed.getVariations() != null) {
                        for (SeedVariation variationSeed : productSeed.getVariations()) {
                            if (variationSeed == null || variationSeed.getPrice() == null) {
                                continue;
                            }
                            VariationEntity v = new VariationEntity();
                            v.setProductEntity(product);
                            v.setPrice(variationSeed.getPrice());
                            v.setPriceDiscount(
                                    variationSeed.getDiscountPrice() != null
                                            ? variationSeed.getDiscountPrice()
                                            : variationSeed.getPrice()
                            );
                            v.setStock(
                                    variationSeed.getStock() != null ? variationSeed.getStock() : 0
                            );

                            // Gắn option combination dựa trên tiers/options đã tạo
                            if (variationSeed.getOptions() != null) {
                                for (SeedOptionRef optRef : variationSeed.getOptions()) {
                                    if (optRef == null
                                            || optRef.getAttributeName() == null
                                            || optRef.getValueName() == null) {
                                        continue;
                                    }
                                    String tierKey = optRef.getAttributeName().trim().toLowerCase(Locale.ROOT);
                                    String optKey = optRef.getValueName().trim().toLowerCase(Locale.ROOT);

                                    Map<String, OptionsEntity> optionsByValue = tierOptionLookup.get(tierKey);
                                    if (optionsByValue == null) {
                                        continue;
                                    }
                                    OptionsEntity optEntity = optionsByValue.get(optKey);
                                    if (optEntity == null) {
                                        continue;
                                    }

                                    VariationOptionEntity vo = new VariationOptionEntity();
                                    vo.setVariationEntity(v);
                                    vo.setOptionsEntity(optEntity);
                                    v.getVariationOptions().add(vo);
                                }
                            }

                            variations.add(v);
                        }
                    }
                }

                product.setVariations(variations);

                productRepo.save(product);
            }
        }
    }

    private CategoryEntity resolveCategory(SeedCategoryRef ref) {
        if (ref == null || ref.getValue() == null || ref.getValue().isEmpty()) {
            throw new IllegalArgumentException("categoryRef path is required in seed JSON");
        }
        List<String> path = ref.getValue();
        List<CategoryEntity> all = categoryRepo.findAllByDeletedAtIsNull();

        for (CategoryEntity candidate : all) {
            if (!Objects.equals(candidate.getName(), path.get(path.size() - 1))) {
                continue;
            }
            if (matchesPath(candidate, path)) {
                return candidate;
            }
        }

        throw new IllegalStateException("Category path not found: " + String.join(" > ", path));
    }

    private boolean matchesPath(CategoryEntity leaf, List<String> path) {
        int index = path.size() - 1;
        CategoryEntity current = leaf;

        while (current != null && index >= 0) {
            if (!Objects.equals(current.getName(), path.get(index))) {
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

    private Optional<AttributeValuesEntity> findAttributeValue(AttributesEntity attribute, String valueText) {
        if (attribute.getAttributeValues() == null) {
            return Optional.empty();
        }
        return attribute.getAttributeValues()
                .stream()
                .filter(v -> v.getValueText() != null && v.getValueText().trim().equals(valueText))
                .findFirst();
    }

    @Data
    public static class SeedConfig {
        private String thumbnailPlaceholder;
        private SeedAccountRef account;
        private SeedCategoryRef categoryRef;
        private List<SeedProduct> products;
    }

    @Data
    public static class SeedAccountRef {
        private String email;
    }

    @Data
    public static class SeedCategoryRef {
        private String by;
        private List<String> value;
    }

    @Data
    public static class SeedProduct {
        private String name;
        private String description;
        private Long weight;
        private String type;
        private SeedPricing pricing;
        private List<SeedAttribute> attributes;
        private List<SeedAttributeForVariation> attributesForVariations;
        private List<SeedVariation> variations;
    }

    @Data
    public static class SeedPricing {
        private BigDecimal price;
        private BigDecimal discountPrice;
        private Integer stock;
    }

    @Data
    public static class SeedAttribute {
        private String attributeName;
        private String valueName;
    }

    @Data
    public static class SeedAttributeForVariation {
        private String attributeName;
        private List<String> values;
    }

    @Data
    public static class SeedVariation {
        private BigDecimal price;
        private BigDecimal discountPrice;
        private Integer stock;
        private List<SeedOptionRef> options;
    }

    @Data
    public static class SeedOptionRef {
        private String attributeName;
        private String valueName;
    }
}

