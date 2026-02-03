package com.chobbi.server.catalog.services.impl;

import com.chobbi.server.catalog.domain.AttributesRule;
import com.chobbi.server.catalog.dto.*;
import com.chobbi.server.catalog.entity.*;
import com.chobbi.server.catalog.enums.AttributeTypesEnums;
import com.chobbi.server.catalog.repo.*;
import com.chobbi.server.catalog.services.CategoryServices;
import com.chobbi.server.catalog.services.ProductServices;
import com.chobbi.server.common.BaseEntity;
import com.chobbi.server.entity.ShopEntity;
import com.chobbi.server.exception.BusinessException;
import com.chobbi.server.storage.FolderTypeEnum;
import com.chobbi.server.storage.services.FilesStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final FilesStorageService filesStorageService;
    private final VariationRepo variationRepo;
    private final VariationOptionRepo variationOptionRepo;
    private final OptionsRepo optionRepo;
    private final TierRepo tierRepo;
    private final CategoryRepo categoryRepo;

    @Override
    @Transactional
    public void createProduct(ProductRequest req, MultipartFile[] media) {

        ShopEntity shopEntity = shopRepo.findById(req.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        CategoryEntity categoryEntity = categoryServices.getLeafCategoryOrThrow(req.getCategoryId());
        ProductEntity product = createBaseProduct(req, shopEntity, categoryEntity);
        processAttributes(product, req.getAttributes(), categoryEntity);
        Map<String, MultipartFile> mapImages = validateImages(req, media);
        processProductImages(req, media, mapImages, product);

        boolean hasTiers = req.getTiers() != null;
        if(!hasTiers) {
            processSingleVariation(req, product);
        } else {
            TierValidationResult tierValidationResult = validateTiers(req, idLookupTierOptionFromDb(product.getId()));
            Map<String, ProcessedTier> normalizeTierOptionMap = tierValidationResult.getNormalizedTierOptions();
            validateVariations(
                    req,
                    normalizeTierOptionMap,
                    idLookupTierOptionFromDb(product.getId()),
                    tierValidationResult.getCountCartesian()
            );
            Map<String, OptionsEntity> mapOptionsEntities = processTierOptions(product, normalizeTierOptionMap);
            processOptionImages(req, mapOptionsEntities, mapImages);
            processVariations(req, product, mapOptionsEntities);
        }
        productRepo.save(product);
    }

    @Override
    public ReadProductDto readProduct(Long productId) {
        ProductEntity product = productRepo.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ReadProductDto dto = new ReadProductDto();
        dto.setProductId(product.getId());
        dto.setProductName(product.getName());
        dto.setDescription(product.getDescription());

        dto.setImages(product.getProductImages().stream()
                .filter(img -> img.getDeletedAt() == null)
                .map(img -> new ReadProductImageDto(img.getId(), img.getPath(), img.getSortOrder()))
                .toList());

        List<ReadProductTierDto> tierDtos = new ArrayList<>();
        List<ReadProductOptionImagesDto> optionImageDtos = new ArrayList<>();

        for (TierEntity tier : product.getTiers()) {
            if (tier.getDeletedAt() != null) {
                continue;
            }
            ReadProductTierDto tierDto = new ReadProductTierDto();
            tierDto.setId(tier.getId());
            tierDto.setName(tier.getName());
            tierDto.setHasImages(tier.getHasImages());

            List<ReadProductTierOptionDto> optionDtos = tier.getOptions().stream()
                    .filter(opt -> opt.getDeletedAt() == null)
                    .map(opt -> {
                        if (opt.getImgPath() != null) {
                            optionImageDtos.add(new ReadProductOptionImagesDto(tier.getId(), opt.getId(), opt.getImgPath()));
                        }
                        return new ReadProductTierOptionDto(opt.getId(), opt.getName());
                    })
                    .toList();

            tierDto.setOptions(optionDtos);
            tierDtos.add(tierDto);
        }
        dto.setTiers(tierDtos);
        dto.setOptionImages(optionImageDtos);

        dto.setVariations(product.getVariations().stream()
                .filter(v -> v.getDeletedAt() == null)
                .map(v -> {
                    ReadProductVariationDto vDto = new ReadProductVariationDto();
                    vDto.setPrice(v.getPrice());
                    vDto.setStock(v.getStock());

                    List<ReadProductVariationOptionDto> combinations = v.getVariationOptions().stream()
                            .map(vo -> new ReadProductVariationOptionDto(
                                    vo.getOptionsEntity().getTierEntity().getId(),
                                    vo.getOptionsEntity().getId()))
                            .toList();

                    vDto.setOptionCombination(combinations);
                    return vDto;
                })
                .toList());

        Set<Long> productValueIds = product.getProductAttributes().stream()
                .map(pa -> pa.getAttributeValuesEntity().getId())
                .collect(Collectors.toSet());

        List<ReadProductAttributes> attributeDefinitions = product.getCategoryEntity().getAttributes().stream()
                .map(attr -> {
                    List<ReadProductAttributeValueDto> valueDtos = attr.getAttributeValues().stream()
                            .filter(val -> !val.getIsCustom() || productValueIds.contains(val.getId()))
                            .map(val -> {
                                String displayValue = switch (attr.getType()) {
                                    case TEXT -> val.getValueText();
                                    case NUMBER -> String.valueOf(val.getValueNumber());
                                    case BOOLEAN -> String.valueOf(val.getValueBoolean());
                                    case DATE -> String.valueOf(val.getValueDate());
                                };
                                return new ReadProductAttributeValueDto(val.getId(), displayValue);
                            })
                            .toList();

                    return new ReadProductAttributes(
                            attr.getId(),
                            attr.getName(),
                            attr.getIsRequired(),
                            attr.getIsCustomAllow(),
                            attr.getIsMultipleAllow(),
                            attr.getType().name(),
                            valueDtos
                    );
                })
                .toList();
        dto.setAttributes(attributeDefinitions);

        Map<Long, List<Long>> selectedMap = product.getProductAttributes().stream()
                .collect(Collectors.groupingBy(
                        pa -> pa.getAttributesEntity().getId(),
                        Collectors.mapping(pa -> pa.getAttributeValuesEntity().getId(), Collectors.toList())
                ));

        dto.setSelectedAttributes(selectedMap.entrySet().stream()
                .map(entry -> new ReadProductSelectedAttributes(entry.getKey(), entry.getValue()))
                .toList());


        dto.setSelectedCategoryId(product.getCategoryEntity().getId());
        dto.setCategoryTree(categoryServices.getTree());

        return dto;
    }

    @Override
    @Transactional
    public void updateProduct(ProductRequest req, MultipartFile[] media) {
        ProductEntity product = productRepo.findByIdAndDeletedAtIsNull(req.getProductId())
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        product.setName(req.getName());
        product.setDescription(req.getDescription());

        product.getProductAttributes().clear();
        CategoryEntity category = categoryRepo.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategoryEntity(category);
        processAttributes(product, req.getAttributes(), category);

        Map<String, MultipartFile> mapImages = validateImages(req, media);
        processProductImages(req, media, mapImages, product);

        boolean reqHasTiers = req.getTiers() != null && !req.getTiers().isEmpty();

        if (!reqHasTiers) {
            product.getTiers().stream()
                    .filter(t -> t.getDeletedAt() == null)
                    .forEach(t -> {
                        t.softDelete();
                        t.getOptions().forEach(BaseEntity::softDelete);
                    });

            processSingleVariation(req, product);
        } else {
            TierValidationResult tierValidationResult = validateTiers(req, idLookupTierOptionFromDb(product.getId()));
            Map<String, ProcessedTier> normalizeTierOptionMap = tierValidationResult.getNormalizedTierOptions();

            validateVariations(
                    req,
                    normalizeTierOptionMap,
                    idLookupTierOptionFromDb(product.getId()),
                    tierValidationResult.getCountCartesian()
            );

            Map<String, OptionsEntity> mapOptionsEntities = processTierOptions(product, normalizeTierOptionMap);

            processOptionImages(req, mapOptionsEntities, mapImages);

            processVariations(req, product, mapOptionsEntities);
        }

        productRepo.save(product);
    }

    private ProductEntity createBaseProduct(ProductRequest req, ShopEntity shopEntity, CategoryEntity categoryEntity) {
        ProductEntity product = new ProductEntity();
        product.setName(req.getName());
        product.setShopEntity(shopEntity);
        product.setCategoryEntity(categoryEntity);
        product.setDescription(req.getDescription());
        return product;
    }

    private void processAttributes(
            ProductEntity product,
            List<ProductAttributeDto> reqAttributes,
            CategoryEntity categoryEntity) {
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

        Set<Long> categoryAttributeIds = categoryAttributes.stream()
                .map(AttributesEntity::getId).collect(Collectors.toSet());
        Set<Long> requiredAttributeIds = ruleMap.entrySet().stream()
                .filter(e -> e.getValue().isRequired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        Set<Long> reqAttributeIds = new HashSet<>();
        for (ProductAttributeDto attr : reqAttributes) {
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
        List<ProductAttributeDto> finalAttributes = new ArrayList<>();

        for (ProductAttributeDto attr : reqAttributes) {
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
                    if (!attr.getCustomValues().isEmpty()) {
                        if (attr.getCustomValues().size() != 1) {
                            throw new RuntimeException("DATE attribute must have exactly one custom value");
                        }
                        try {
                            LocalDate.parse(attr.getCustomValues().getFirst().trim()); // ISO-8601
                        } catch (DateTimeParseException e) {
                            throw new RuntimeException("Invalid DATE format. Expected yyyy-MM-dd");
                        }
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
        List<ProductAttributesEntity> listProductAttributeEntity = new ArrayList<>();
        for (ProductAttributeDto attr : finalAttributes) {
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
    }

    private void processProductImages(
            ProductRequest req, MultipartFile[] media,
            Map<String, MultipartFile> mapImages, ProductEntity product) {

        Map<Long, ProductImagesEntity> existingImgMap = product.getProductImages().stream()
                .filter(img -> img.getDeletedAt() == null)
                .collect(Collectors.toMap(ProductImagesEntity::getId, img -> img));

        Set<Long> reqImgIds = req.getImages().stream()
                .map(ProductImageDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        product.getProductImages().stream()
                .filter(img -> img.getDeletedAt() == null && !reqImgIds.contains(img.getId()))
                .forEach(BaseEntity::softDelete);

        for (ProductImageDto item : req.getImages()) {
            ProductImagesEntity imgEntity;

            if (item.getId() != null && existingImgMap.containsKey(item.getId())) {
                imgEntity = existingImgMap.get(item.getId());
            } else {
                MultipartFile file = mapImages.get(item.getName());
                if (file == null) continue;

                String savedImgPath = filesStorageService.transferStorage(file, FolderTypeEnum.PRODUCTS);
                imgEntity = new ProductImagesEntity();
                imgEntity.setPath(savedImgPath);
                imgEntity.setProductEntity(product);
                product.getProductImages().add(imgEntity);
            }

            imgEntity.setSortOrder(item.getSort() != null ? item.getSort() : 0);

            if (imgEntity.getSortOrder() == 1) {
                product.setThumbnail(imgEntity.getPath());
            }
        }
    }

    private Map<String, MultipartFile> validateImages(ProductRequest req, MultipartFile[] media) {

        // validate image upload
        List<ProductImageDto> reqProductImages = req.getImages();
        List<ProductOptionImageDto> reqOptionImages = req.getOptionImages() != null ?
                req.getOptionImages() : Collections.emptyList();
        if (!reqOptionImages.isEmpty() && (req.getTiers() == null || req.getTiers().isEmpty())) {
            throw new RuntimeException("Không thể gửi ảnh tùy chọn (optionImages) khi sản phẩm không có phân loại (tiers).");
        }

        Set<String> reqImageNames = new HashSet<>();
        for (ProductImageDto item : reqProductImages) {
            if (!reqImageNames.add(item.getName())) {
                throw new RuntimeException("Duplicate image name: " + item.getName());
            }
        }

        Set<String> imgOptionNames = new HashSet<>();
        for (ProductOptionImageDto item : reqOptionImages) {
            if (!reqImageNames.add(item.getImageName())) {
                throw new RuntimeException("Duplicate image name: " + item.getImageName());
            }
            if (!imgOptionNames.add(item.getOptionName().toLowerCase(Locale.ROOT))) {
                throw new RuntimeException("Duplicate option name: " + item.getOptionName());
            }
        }
        Set<String> optionNames = new HashSet<>();
        if (req.getTiers() != null && !req.getTiers().isEmpty()) {
            for (ProductTierDto tier : req.getTiers()) {
                if (tier.getHasImages()) {
                    for (ProductTierOptionDto opt : tier.getOptions()) {
                        optionNames.add(opt.getName().toLowerCase(Locale.ROOT));
                    }
                }
            }
        }
        if (!imgOptionNames.isEmpty() && !imgOptionNames.containsAll(optionNames)) {
            throw new RuntimeException("Option images option name do not belong to has images tier option names:");
        }

        Map<String, MultipartFile> mapImages = new HashMap<>();

        if (media != null) {
            for (MultipartFile item : media) {
                String fileName = item.getOriginalFilename();
                if (mapImages.containsKey(fileName)) {
                    throw new RuntimeException("Duplicate image name in upload: " + fileName);
                }
                mapImages.put(fileName, item);
            }
        }

        return mapImages;
    }

    private Map<String, OptionsEntity> processTierOptions(ProductEntity product, Map<String, ProcessedTier> normalizeTierOptionMap) {
        Map<Long, TierEntity> existingTierMap = product.getTiers().stream()
                .filter(t -> t.getDeletedAt() == null)
                .collect(Collectors.toMap(TierEntity::getId, t -> t));

        Set<Long> reqTierIds = normalizeTierOptionMap.values().stream()
                .map(ProcessedTier::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (TierEntity tier : product.getTiers()) {
            if (tier.getDeletedAt() == null && (tier.getId() == null || !reqTierIds.contains(tier.getId()))) {
                tier.softDelete();
                tier.getOptions().forEach(BaseEntity::softDelete);
            }
        }

        Map<String, OptionsEntity> mapOptionsEntities = new HashMap<>();

        for (ProcessedTier processedTier : normalizeTierOptionMap.values()) {
            TierEntity tierEntity;

            if (processedTier.getId() != null && existingTierMap.containsKey(processedTier.getId())) {
                tierEntity = existingTierMap.get(processedTier.getId());
            } else {
                tierEntity = new TierEntity();
                tierEntity.setProductEntity(product);
                product.getTiers().add(tierEntity);
            }

            tierEntity.setName(processedTier.getName());
            tierEntity.setHasImages(processedTier.isHasImages());

            processOptions(tierEntity, processedTier, mapOptionsEntities);
        }
        return mapOptionsEntities;
    }

    private void processOptions(TierEntity tierEntity, ProcessedTier processedTier, Map<String, OptionsEntity> mapOptionsEntities) {
        Map<Long, OptionsEntity> existingOptMap = tierEntity.getOptions().stream()
                .filter(o -> o.getDeletedAt() == null)
                .collect(Collectors.toMap(OptionsEntity::getId, o -> o));

        Set<Long> reqOptIds = processedTier.getOptionsMap().values().stream()
                .map(ProcessedOption::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        tierEntity.getOptions().stream()
                .filter(o -> o.getDeletedAt() == null && !reqOptIds.contains(o.getId()))
                .forEach(BaseEntity::softDelete);

        for (ProcessedOption processedOpt : processedTier.getOptionsMap().values()) {
            OptionsEntity optionEntity;

            if (processedOpt.getId() != null && existingOptMap.containsKey(processedOpt.getId())) {
                optionEntity = existingOptMap.get(processedOpt.getId());
            } else {
                optionEntity = new OptionsEntity();
                optionEntity.setTierEntity(tierEntity);
                tierEntity.getOptions().add(optionEntity);
            }

            optionEntity.setName(processedOpt.getName());

            String key = processedTier.getName() + ":" + processedOpt.getName();
            mapOptionsEntities.put(key, optionEntity);
        }
    }

    private void processOptionImages(ProductRequest req, Map<String, OptionsEntity> mapOptionsEntities, Map<String, MultipartFile> mapImages) {
        Set<String> reqOptionImageKeys = new HashSet<>();
        if (req.getOptionImages() != null) {
            req.getOptionImages().forEach(item ->
                    reqOptionImageKeys.add(item.getTierName().toLowerCase() + ":" + item.getOptionName().toLowerCase())
            );
        }

        mapOptionsEntities.forEach((key, optionEntity) -> {
            if (optionEntity.getTierEntity().getHasImages()) {

                if (reqOptionImageKeys.contains(key)) {
                    ProductOptionImageDto imgDto = req.getOptionImages().stream()
                            .filter(dto -> (dto.getTierName().toLowerCase() + ":" + dto.getOptionName().toLowerCase()).equals(key))
                            .findFirst().get();

                    MultipartFile file = mapImages.get(imgDto.getImageName());
                    if (file != null) {
                        String savedImgPath = filesStorageService.transferStorage(file, FolderTypeEnum.PRODUCTS);
                        optionEntity.setImgPath(savedImgPath);
                    }
                } else {
                    optionEntity.setImgPath(null);
                }
            }
        });
    }

    private void processVariations(ProductRequest req, ProductEntity product, Map<String, OptionsEntity> mapOptionsEntities) {
        Map<Long, VariationEntity> existingVarMap = product.getVariations().stream()
                .filter(v -> v.getDeletedAt() == null)
                .collect(Collectors.toMap(VariationEntity::getId, v -> v));

        Set<Long> reqVarIds = req.getVariations().stream()
                .map(ProductVariationDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        product.getVariations().stream()
                .filter(v -> v.getDeletedAt() == null && !reqVarIds.contains(v.getId()))
                .forEach(BaseEntity::softDelete);

        for (ProductVariationDto reqVar : req.getVariations()) {
            VariationEntity varEntity;

            if (reqVar.getId() != null && existingVarMap.containsKey(reqVar.getId())) {
                varEntity = existingVarMap.get(reqVar.getId());
                varEntity.getVariationOptions().clear();
            } else {
                varEntity = new VariationEntity();
                varEntity.setProductEntity(product);
                product.getVariations().add(varEntity);
            }

            varEntity.setPrice(reqVar.getPrice());
            varEntity.setStock(reqVar.getStock());

            for (ProductRequestOptionCombination comb : reqVar.getOptionCombination()) {
                String key = comb.getTierName().toLowerCase().trim() + ":" + comb.getOptionName().toLowerCase().trim();
                OptionsEntity optEntity = mapOptionsEntities.get(key);

                if (optEntity != null) {
                    VariationOptionEntity link = new VariationOptionEntity();
                    link.setVariationEntity(varEntity);
                    link.setOptionsEntity(optEntity);
                    varEntity.getVariationOptions().add(link);
                }
            }
        }
    }

    private void processSingleVariation(ProductRequest req, ProductEntity product) {
        ProductVariationDto reqVariation = validateNoTiers(req);

        VariationEntity variation = product.getVariations().stream()
                .filter(v -> v.getDeletedAt() == null)
                .findFirst()
                .orElseGet(() -> {
                    VariationEntity newVar = new VariationEntity();
                    newVar.setProductEntity(product);
                    product.getVariations().add(newVar);
                    return newVar;
                });

        variation.setPrice(reqVariation.getPrice());
        variation.setStock(reqVariation.getStock());
        variation.getVariationOptions().clear();
    }

    private Map<Long, ProcessedTier> idLookupTierOptionFromDb(Long productId) {
        if (productId == null) {
            return Collections.emptyMap();
        }
        List<TierEntity> existingTiers = tierRepo.findAllByProductEntity_IdAndDeletedAtIsNull(productId);
        return existingTiers.stream().collect(Collectors.toMap(
                TierEntity::getId,
                tierEntity -> {
                    Map<String, ProcessedOption> optionsMap = tierEntity.getOptions().stream()
                            .filter(opt -> opt.getDeletedAt() == null)
                            .collect(Collectors.toMap(
                                    opt -> opt.getName().toLowerCase(Locale.ROOT).trim(),
                                    opt -> new ProcessedOption(opt.getId(), opt.getName())
                            ));

                    return ProcessedTier.builder()
                            .id(tierEntity.getId())
                            .name(tierEntity.getName().toLowerCase(Locale.ROOT).trim())
                            .hasImages(tierEntity.getHasImages())
                            .optionsMap(optionsMap)
                            .build();
                }
        ));
    }

    private ProductVariationDto validateNoTiers(ProductRequest req) {
        List<ProductVariationDto> reqVariations = req.getVariations();
        if (reqVariations.size() != 1) {
            throw new RuntimeException("Invalid variation size");
        }
        return reqVariations.getFirst();
    }

    private TierValidationResult validateTiers(ProductRequest req, Map<Long, ProcessedTier> idLookupTierOption) {
        List<ProductTierDto> reqTiers = req.getTiers();
        Map<String, ProcessedTier> normalizeTierOptionMap = new HashMap<>();
        int countCartesian = 1;
        Set<String> seenTiers = new HashSet<>();
        int hasImagesCount = 0;

        for (ProductTierDto tier : reqTiers) {
            Long tierId = tier.getId();
            ProcessedTier dbTier = null;

            if (tierId != null) {
                dbTier = idLookupTierOption.get(tierId);
                if (dbTier == null) {
                    throw new RuntimeException("Tier ID " + tierId + " không thuộc sản phẩm này!");
                }
            }

            String tierName = tier.getName().toLowerCase(Locale.ROOT).trim();
            boolean hasImages = tier.getHasImages() != null && tier.getHasImages();
            if (hasImages) hasImagesCount++;

            if (!seenTiers.add(tierName)) {
                throw new RuntimeException("Duplicate tier: " + tierName);
            }

            // 2. Xử lý Options bên trong Tier
            Map<String, ProcessedOption> normalizeOptionMap = new HashMap<>();
            Set<String> optionSet = new HashSet<>();
            List<ProductTierOptionDto> reqOptions = tier.getOptions();

            if (reqOptions == null || reqOptions.isEmpty()) {
                throw new RuntimeException("Tier " + tierName + " must have at least one option.");
            }

            for (ProductTierOptionDto option : reqOptions) {
                Long optionId = option.getId();
                if (optionId != null && dbTier != null) {
                    boolean isOptValid = dbTier.getOptionsMap().values().stream()
                            .anyMatch(opt -> opt.getId().equals(optionId));
                    if (!isOptValid) {
                        throw new RuntimeException("Option ID " + optionId + " không thuộc Tier " + tierName);
                    }
                }

                String optionName = option.getName().toLowerCase(Locale.ROOT).trim();
                if (!optionSet.add(optionName)) {
                    throw new RuntimeException("Duplicate option: " + optionName + " in tier " + tierName);
                }

                normalizeOptionMap.put(optionName, new ProcessedOption(optionId, optionName));
            }

            countCartesian *= optionSet.size();
            normalizeTierOptionMap.put(tierName, ProcessedTier.builder()
                    .id(tierId)
                    .name(tierName)
                    .hasImages(hasImages)
                    .optionsMap(normalizeOptionMap)
                    .build());
        }
        if (hasImagesCount != 1) {
            throw new RuntimeException("Sản phẩm có biến thể phải có duy nhất 1 nhóm phân loại (Tier) có hình ảnh.");
        }

        return new TierValidationResult(normalizeTierOptionMap, countCartesian);
    }

    private void validateVariations(
            ProductRequest req,
            Map<String, ProcessedTier> normalizeTierOptionMap,
            Map<Long, ProcessedTier> dbTierOptionLookup,
            int countCartesian
    ) {
        List<ProductVariationDto> reqVariations = req.getVariations();

        if (reqVariations == null || reqVariations.size() != countCartesian) {
            throw new RuntimeException("Số lượng biến thể không khớp với tổ hợp Tiers/Options.");
        }

        Set<Set<String>> seenOptionCombinations = new HashSet<>();

        for (ProductVariationDto reqVariation : reqVariations) {
            List<ProductRequestOptionCombination> combs = reqVariation.getOptionCombination();

            if (combs == null || combs.size() != normalizeTierOptionMap.size()) {
                throw new RuntimeException("Biến thể thiếu thông tin nhóm phân loại.");
            }

            Set<String> seenTierInVar = new HashSet<>();
            Set<String> currentCombinationStrings = new HashSet<>();

            for (ProductRequestOptionCombination item : combs) {
                String normTierName = item.getTierName().toLowerCase(Locale.ROOT).trim();
                String normOptName = item.getOptionName().toLowerCase(Locale.ROOT).trim();
                Long reqTierId = item.getTierId();
                Long reqOptId = item.getOptionId();

                ProcessedTier reqTier = normalizeTierOptionMap.get(normTierName);
                if (reqTier == null) {
                    throw new RuntimeException("Nhóm '" + normTierName + "' không khớp với danh sách Tiers.");
                }

                ProcessedOption reqOpt = reqTier.getOptionsMap().get(normOptName);
                if (reqOpt == null) {
                    throw new RuntimeException("Lựa chọn '" + normOptName + "' không thuộc nhóm '" + normTierName + "'.");
                }

                if (reqTierId != null) {
                    ProcessedTier dbTier = dbTierOptionLookup.get(reqTierId);
                    if (dbTier == null) {
                        throw new RuntimeException("Tier ID " + reqTierId + " không tồn tại hoặc không thuộc sản phẩm này.");
                    }
                }

                if (reqOptId != null && reqTierId != null) {
                    ProcessedTier dbTier = dbTierOptionLookup.get(reqTierId);
                    boolean optExistsInDb = dbTier.getOptionsMap().values().stream()
                            .anyMatch(o -> o.getId().equals(reqOptId));
                    if (!optExistsInDb) {
                        throw new RuntimeException("Option ID " + reqOptId + " không thuộc Tier ID " + reqTierId + " trong DB.");
                    }
                }

                if (!seenTierInVar.add(normTierName)) {
                    throw new RuntimeException("Nhóm phân loại lặp lại: " + normTierName);
                }
                currentCombinationStrings.add(normTierName + ":" + normOptName);
            }

            if (!seenOptionCombinations.add(currentCombinationStrings)) {
                throw new RuntimeException("Tổ hợp biến thể bị trùng lặp.");
            }
        }
    }
}