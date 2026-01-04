package com.chobbi.server.catalog.services.impl;

import com.chobbi.server.catalog.dto.*;
import com.chobbi.server.catalog.entity.*;
import com.chobbi.server.catalog.services.ProductServices;
import com.chobbi.server.entity.ShopEntity;
import com.chobbi.server.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductServicesImp implements ProductServices {

    private final ProductRepo productRepo;
    private final ShopRepo shopRepo;
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

        //CategoryEntity category = categoryServices.getLeafCategoryOrThrow(req.getCategoryId());
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(req.getCategoryId());
        ProductEntity product = new ProductEntity();
        product.setName(req.getName());
        product.setShopEntity(shopEntity);
        product.setCategoryEntity(categoryEntity);
        product.setDescription(req.getDescription());

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
                                    //optionEntity.getVariationOptions().add(variationOptionEntity);
                                    // check lại trường hợp nếu có hoặc ko có đoạn trên insert trên db có khác ko
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
