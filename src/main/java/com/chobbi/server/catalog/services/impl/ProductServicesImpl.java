package com.chobbi.server.catalog.services.impl;

import com.chobbi.server.catalog.domain.AttributesRule;
import com.chobbi.server.catalog.dto.*;
import com.chobbi.server.catalog.entity.*;
import com.chobbi.server.catalog.enums.AttributeTypesEnums;
import com.chobbi.server.catalog.services.CategoryServices;
import com.chobbi.server.catalog.services.ProductServices;
import com.chobbi.server.entity.ShopEntity;
import com.chobbi.server.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServicesImpl implements ProductServices {

    private final ProductRepo productRepo;
    private final ShopRepo shopRepo;
    private final CategoryServices categoryServices;
    private final AttributesRepo attributesRepo;
    private final AttributeValuesRepo attributeValuesRepo;
    private final VariationRepo variationRepo;
    private final VariationOptionRepo variationOptionRepo;
    private final OptionsRepo optionRepo;
    private final TierRepo tierRepo;

    @Override
    @Transactional
    public void createProduct(CreateProductRequest req) {

//
        ShopEntity shopEntity = shopRepo.findById(req.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        CategoryEntity categoryEntity = categoryServices.getLeafCategoryOrThrow(req.getCategoryId());
        ProductEntity product = new ProductEntity();
        product.setName(req.getName());
        product.setShopEntity(shopEntity);
        product.setCategoryEntity(categoryEntity);
        product.setDescription(req.getDescription());

        // validate attributes
        // rule map
        List<AttributesEntity> categoryAttributes = categoryEntity.getAttributes();
        Map<Long, AttributesRule> ruleMap = new HashMap<>();
        for (AttributesEntity attr : categoryAttributes) {
            AttributesRule rule = new AttributesRule();
            rule.setRequired(attr.getIsRequired());
            rule.setMultipleAllow(attr.getIsMultipleAllow());
            rule.setCustomAllow(attr.getIsCustomAllow());
            rule.setType(attr.getType());
            ruleMap.put(attr.getId(), rule);
        }

        // validate required attributes
        List<CreateProductAttributeDto> reqAttributes = req.getAttributes();
        Set<Long> categoryAttributeIds = categoryAttributes.stream()
                .map(AttributesEntity::getId).collect(Collectors.toSet());
        Set<Long> requiredAttributeIds = ruleMap.entrySet().stream()
                .filter(e -> e.getValue().isRequired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        Set<Long> reqAttributeIds = new HashSet<>();
        for (CreateProductAttributeDto attr : reqAttributes) {
            if (!reqAttributeIds.add(attr.getId())) {
                throw new RuntimeException("Duplicate attribute");
            }
        }
        if (!categoryAttributeIds.containsAll(reqAttributeIds)) {
            throw new RuntimeException("Invalid attribute id");
        }
        if (!reqAttributeIds.containsAll(requiredAttributeIds)) {
            throw new RuntimeException("Required attribute not found");
        }
        List<CreateProductAttributeDto> finalAttributes = new ArrayList<>();

        for (CreateProductAttributeDto attr : reqAttributes) {
            AttributesRule rule = ruleMap.get(attr.getId());
            boolean hasValues = attr.getValueIds() != null && !attr.getValueIds().isEmpty();
            boolean hasCustomValues = attr.getCustomValues() != null && !attr.getCustomValues().isEmpty();
            List<Long> valueIds =
                    attr.getValueIds() == null ? List.of() : attr.getValueIds();
            List<String> customValues =
                    attr.getCustomValues() == null ? List.of() : attr.getCustomValues();
            Set<Long> setValueIds = new HashSet<>();
            for (Long id : valueIds) {
                if (!setValueIds.add(id)) {
                    throw new RuntimeException("Duplicate value id " + id);
                }
            }
            Set<String> seenCustomValues = new HashSet<>();
            for (String value : customValues) {
                if (!seenCustomValues.add(value.toLowerCase(Locale.ROOT))) {
                    throw new RuntimeException("Duplicate custom value: " + value);
                }
            }
            AttributeTypesEnums type = rule.getType();
            switch (type) {
                case DATE -> {
                    if (hasValues) {
                        throw new RuntimeException("DATE attribute must not have valueIds");
                    }
                    if (!hasCustomValues) {
                        throw new RuntimeException("DATE attribute must have a custom value");
                    }
                    if (attr.getCustomValues().size() != 1) {
                        throw new RuntimeException("DATE attribute must have exactly one custom value");
                    }
                    try {
                        LocalDate.parse(attr.getCustomValues().getFirst().trim()); // ISO-8601
                    } catch (DateTimeParseException e) {
                        throw new RuntimeException("Invalid DATE format. Expected yyyy-MM-dd");
                    }
                }
                case BOOLEAN -> {
                    if (hasCustomValues) {
                        throw new RuntimeException("BOOLEAN attribute must not have custom values");
                    }
                    if (!hasValues) {
                        throw new RuntimeException("BOOLEAN attribute must have a value");
                    }
                    if (attr.getValueIds().size() != 1) {
                        throw new RuntimeException("BOOLEAN attribute must have exactly one value");
                    }
                }
                case NUMBER -> {
                    if (hasCustomValues) {
                        try {
                            for (String val : customValues) {
                                Double.parseDouble(val);
                            }
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid number format. Expected a number");
                        }
                    }
                }
            }
            if (!rule.isCustomAllow() && hasCustomValues) {
                throw new RuntimeException("This required attributes don't allow custom value");
            }
            int totalValues = valueIds.size() + customValues.size();
            if (rule.isRequired() && totalValues == 0) {
                throw new RuntimeException("Required attributes must have at least one value");
            }
            if (!rule.isMultipleAllow() && totalValues > 1) {
                throw new RuntimeException("Multiple attributes must have at most one value");
            }

            // TỐI ƯU NOTE: check tối ưu n + 1 query nhé
            Set<Long> attributeValuesEntityIds = attributesRepo.getReferenceById(
                    attr.getId())
                    .getAttributeValues()
                    .stream()
                    .map(AttributeValuesEntity::getId)
                    .collect(Collectors.toSet());
            if (hasValues && !attributeValuesEntityIds.containsAll(setValueIds)) {
                throw new RuntimeException("Value ids not belong to attribute");
            }

            if (hasValues || hasCustomValues) {
                finalAttributes.add(attr);
            }
        }

        // save db finalAttributes
        // tìm hiểu lại jpa, refernece by id, lazy load, rồi cascade. hiểu bản chất
        List<ProductAttributesEntity> listProductAttributeEntity = new ArrayList<>();
        for (CreateProductAttributeDto attr : finalAttributes) {
            AttributesRule rule = ruleMap.get(attr.getId());
            AttributesEntity attributesEntity = attributesRepo.findById(attr.getId()).orElseThrow();
            List<AttributeValuesEntity> listAttributeValuesEntity = new ArrayList<>();
            List<Long> valueIds = attr.getValueIds();
            List<String> customValues = attr.getCustomValues();
            if (valueIds != null && !valueIds.isEmpty()) {
                for (Long id : valueIds) {
                    AttributeValuesEntity valuesEntity = attributeValuesRepo.getReferenceById(id);
                    listAttributeValuesEntity.add(valuesEntity);
                }
            }
            if (customValues != null && !customValues.isEmpty()) {
                AttributeTypesEnums type = rule.getType();
                switch (type) {
                    case DATE -> {
                        AttributeValuesEntity valuesEntity = new AttributeValuesEntity();
                        valuesEntity.setIsCustom(true);
                        valuesEntity.setValueDate(LocalDate.parse(attr.getCustomValues().getFirst().trim()));
                        valuesEntity.setAttributesEntity(attributesEntity);
                        listAttributeValuesEntity.add(valuesEntity);
                    }
                    case TEXT -> {
                        for (String value : customValues) {
                            AttributeValuesEntity valuesEntity = new AttributeValuesEntity();
                            valuesEntity.setIsCustom(true);
                            valuesEntity.setValueText(value);
                            valuesEntity.setAttributesEntity(attributesEntity);
                            listAttributeValuesEntity.add(valuesEntity);
                        }
                    }
                    case NUMBER -> {
                        for (String value : customValues) {
                            AttributeValuesEntity valuesEntity = new AttributeValuesEntity();
                            valuesEntity.setIsCustom(true);
                            valuesEntity.setValueNumber(Double.parseDouble(value));
                            valuesEntity.setAttributesEntity(attributesEntity);
                            listAttributeValuesEntity.add(valuesEntity);
                        }
                    }
                }
            }

            attributesEntity.getAttributeValues().addAll(listAttributeValuesEntity);

            for (AttributeValuesEntity val: listAttributeValuesEntity) {
                System.out.println(val.getValueText());
                ProductAttributesEntity productAttributesEntity = new ProductAttributesEntity();
                productAttributesEntity.setProductEntity(product);
                productAttributesEntity.setAttributesEntity(attributesEntity);
                productAttributesEntity.setAttributeValuesEntity(val);
                if (val.getIsCustom()) {
                    val.getProductAttributes().add(productAttributesEntity);
                }

                listProductAttributeEntity.add(productAttributesEntity);
            }
        }

        product.getProductAttributes().addAll(listProductAttributeEntity);

        // Product tier options
        boolean hasTiers = req.getTiers() != null;
        if(!hasTiers) {
            CreateProductVariationDto reqVariation = validateNoTiers(req);
            VariationEntity variation = new VariationEntity();
            variation.setPrice(reqVariation.getPrice());
            variation.setStock(reqVariation.getStock());
            variation.setProductEntity(product);
            product.getVariations().add(variation);
        } else {
            TierValidationResult tierValidationResult = validateTiers(req);
            Map<String, Set<String>> normalizeTierOptionMap = tierValidationResult.getNormalizedTierOptions();
            List<CreateProductVariationDto> reqVariations = validateVariations(
                    req,
                    normalizeTierOptionMap,
                    tierValidationResult.getCountCartesian()
            );
            // Add tiers + options from tiers request
            for (Map.Entry<String, Set<String>> entry : normalizeTierOptionMap.entrySet()) {
                TierEntity tierEntity = new TierEntity();
                tierEntity.setName(entry.getKey());
                tierEntity.setProductEntity(product);
                product.getTiers().add(tierEntity);
                for (String option : entry.getValue()) {
                    OptionsEntity optionEntity = new OptionsEntity();
                    optionEntity.setName(option);
                    optionEntity.setTierEntity(tierEntity);
                    tierEntity.getOptions().add(optionEntity);
                }
            }
            // Add variations and variation_option for variation_request
            for (CreateProductVariationDto reqVariation : reqVariations) {
                VariationEntity variationEntity = new VariationEntity();
                variationEntity.setPrice(reqVariation.getPrice());
                variationEntity.setStock(reqVariation.getStock());
                variationEntity.setProductEntity(product);
                for (CreateProductRequestOptionCombination optionCombination : reqVariation.getOptionCombination()) {
                    String tierName = optionCombination.getTierName().toLowerCase();
                    String optionName = optionCombination.getOptionName().toLowerCase();
                    List<TierEntity> listTierEntity = product.getTiers();
                    for (TierEntity tierEntity : listTierEntity) {
                        if (tierEntity.getName().equals(tierName)) {
                            List<OptionsEntity> ops = tierEntity.getOptions();
                            for (OptionsEntity optionEntity : ops) {
                                if (optionEntity.getName().equals(optionName)) {
                                    VariationOptionEntity variationOptionEntity = new VariationOptionEntity();
                                    variationOptionEntity.setVariationEntity(variationEntity);
                                    variationOptionEntity.setOptionsEntity(optionEntity);
                                    variationEntity.getVariationOptions().add(variationOptionEntity);
                                }
                            }
                        }
                    }
                }
                product.getVariations().add(variationEntity);
            }
        }

        productRepo.save(product);
    }

    private static CreateProductVariationDto validateNoTiers(CreateProductRequest req) {
        List<CreateProductVariationDto> reqVariations = req.getVariations();
        if (reqVariations.size() != 1) {
            throw new RuntimeException("Invalid variation size");
        }
        return reqVariations.getFirst();
    }

    private static TierValidationResult validateTiers(CreateProductRequest req) {
        List<CreateProductTierDto> reqTiers = req.getTiers();
        Map<String, Set<String>> normalizeTierOptionMap = new HashMap<>();
        int countCartesian = 1;

        Set<String> seenTiers = new HashSet<>();
        for (CreateProductTierDto tier : reqTiers) {
            if(!seenTiers.add(tier.getName().toLowerCase(Locale.ROOT))) {
                throw new RuntimeException("Duplicate tier");
            }
            List<String> options = tier.getOptions();
            Set<String> optionSet  = new HashSet<>();
            for (String option : options) {
                if (!optionSet.add(option.toLowerCase(Locale.ROOT))) {
                    throw new RuntimeException("Duplicate option.");
                }
            }
            countCartesian *= optionSet.size();
            normalizeTierOptionMap.put(tier.getName().toLowerCase(Locale.ROOT), optionSet);
        }
        return new TierValidationResult(normalizeTierOptionMap, countCartesian);
    }

    private static List<CreateProductVariationDto> validateVariations(
            CreateProductRequest req,
            Map<String, Set<String>> normalizeTierOptionMap,
            int countCartesian
    ) {
        List<CreateProductVariationDto> reqVariations = req.getVariations();
        // Check count cartesian build từ tiers request có match với size của req variation k
        if (reqVariations.size() != countCartesian) {
            throw new RuntimeException("Variations count does not match cartesian");
        }
        Set<Set<String>> seenOptionCombinations = new HashSet<>();
        for (CreateProductVariationDto reqVariation : reqVariations) {
            List<CreateProductRequestOptionCombination> comb = reqVariation.getOptionCombination();
            // check xem size của list option combination có trùng với size của list tiers request k
            if (comb == null || comb.isEmpty()) {
                throw new RuntimeException("Empty variation combination");
            }
            if (comb.size() != normalizeTierOptionMap.size()) {
                throw new RuntimeException("Invalid variation size");
            }
            Set<String> seenTierName = new HashSet<>();
            Set<String> seenOptionCombinationName = new HashSet<>();
            for (CreateProductRequestOptionCombination item : comb) {
                String normalizeTierName = item.getTierName().toLowerCase(Locale.ROOT);
                String normalizeOptionName = item.getOptionName().toLowerCase(Locale.ROOT);
                // check xem tier name trong option combination có trùng k
                if (!seenTierName.add(normalizeTierName)) {
                    throw new RuntimeException("Duplicate tier name");
                }
                // check xem tier name trong option combination có tồn tại trong tiers request k
                if (normalizeTierOptionMap.get(normalizeTierName) == null) {
                    throw new RuntimeException("Invalid tier");
                }
                // check xem optionName có thuộc options trong tier options của tiers request k
                if (!normalizeTierOptionMap.get(normalizeTierName).contains(normalizeOptionName)) {
                    throw new RuntimeException("Invalid variation option");
                }
                String key = normalizeTierName + ":" + normalizeOptionName;
                seenOptionCombinationName.add(key);

            }
            // check xem option combination của mỗi variation có bị trùng k
            if (!seenOptionCombinations.add(seenOptionCombinationName)) {
                throw new RuntimeException("Duplicate variation option combination");
            }
        }
        return reqVariations;
    }
}
