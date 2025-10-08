package com.chobbi.server.services.imp;

import com.chobbi.server.dto.*;
import com.chobbi.server.entity.*;
import com.chobbi.server.enums.ProductDtofieldEnums;
import com.chobbi.server.payload.request.ProductRequest;
import com.chobbi.server.repo.*;
import com.chobbi.server.services.CategoryServices;
import com.chobbi.server.services.ProductServices;
import com.chobbi.server.services.TierServices;
import com.chobbi.server.services.VariationServices;
import com.chobbi.server.utils.ProductOptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServicesImp implements ProductServices {

    private final ProductRepo productRepo;
    private final ShopRepo shopRepo;
    private final VariationRepo variationRepo;
    private final VariationOptionRepo variationOptionRepo;
    private final CategoryServices categoryServices;
    private final TierServices tierServices;
    private final VariationServices variationServices;
    private final OptionsRepo optionRepo;
    private final TierRepo tierRepo;

    @Override
    public ProductDto getProduct(Long shopId, Long productId) {
        // Check shop tồn tại
        ShopEntity shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // Check product tồn tại
        ProductEntity product = productRepo.findByIdAndShopEntity_IdAndDeletedAtIsNull(productId, shop.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return buildProductDto(product, Set.of(
                ProductDtofieldEnums.VARIANTS,
                ProductDtofieldEnums.OPTIONS,
                ProductDtofieldEnums.CATEGORIES
        ));
    }

    @Override
    public List<ProductDto> getProducts(Long shopId) {
        // Check shop tồn tại
        ShopEntity shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        List<ProductEntity> productList = productRepo.findAllByShopEntity_IdAndDeletedAtIsNull(shop.getId());

        return productList.stream()
                .map(product -> buildProductDto(product, Set.of(
                        ProductDtofieldEnums.VARIANTS,
                        ProductDtofieldEnums.OPTIONS,
                        ProductDtofieldEnums.CATEGORIES
                )))
                .toList();
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductRequest request) {

        ShopEntity shop = shopRepo.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        CategoryEntity category = categoryServices.getLeafCategoryOrThrow(request.getCategoryId());

        ProductEntity product = new ProductEntity();
        product.setTitle(request.getTitle());
        product.setShopEntity(shop);
        product.setCategoryEntity(category);
        ProductEntity newProduct = productRepo.save(product);

        // 1. Tạo tiers + options
        List<List<OptionsEntity>> tierOptionsMatrix = tierServices.createOrUpdateTiers(newProduct, request.getTiers());

        // 2. Tạo variations + variation_option
        variationServices.createVariations(newProduct, request.getVariations(), tierOptionsMatrix);

        // 3. Trả về dto
        return buildProductDto(newProduct, Set.of(
                ProductDtofieldEnums.VARIANTS,
                ProductDtofieldEnums.OPTIONS,
                ProductDtofieldEnums.CATEGORIES
        ));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductRequest request) {
        // 1. Check shop tồn tại
        ShopEntity shop = shopRepo.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // 2. Lấy product và check shop id match
        ProductEntity product = productRepo.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getShopEntity().getId().equals(shop.getId())) {
            throw new RuntimeException("Product does not belong to this shop");
        }

        // 3. Check category leaf
        CategoryEntity category = categoryServices.getLeafCategoryOrThrow(request.getCategoryId());

        // 4. Update product
        product.setTitle(request.getTitle());
        product.setCategoryEntity(category);
        productRepo.save(product);

        // 5. Merge/update tiers + options
        List<List<OptionsEntity>> tierOptionsMatrix = tierServices.createOrUpdateTiers(product, request.getTiers());

        // 6. Merge/update variations + variation_option
        variationServices.updateVariations(product, request.getVariations(), tierOptionsMatrix);

        // 7. Build ProductDto để trả về
        return buildProductDto(product, Set.of(
                ProductDtofieldEnums.VARIANTS,
                ProductDtofieldEnums.OPTIONS,
                ProductDtofieldEnums.CATEGORIES
        ));
    }

    @Override
    public void deleteProduct(Long shopId, Long productId) {
        // 1. Check shop tồn tại
        ShopEntity shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // 2. Lấy product và check shop id match
        ProductEntity product = productRepo.findByIdAndShopEntity_IdAndDeletedAtIsNull(productId, shop.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getShopEntity().getId().equals(shop.getId())) {
            throw new RuntimeException("Product does not belong to this shop");
        }

        product.softDelete();
        productRepo.save(product);
//
//        // 2. Lấy variations
//        List<VariationEntity> variations = variationRepo.findAllByProductEntity_Id(productId);
//        variations.forEach(VariationEntity::softDelete);
//        variationRepo.saveAll(variations);
//
//        // 3. Lấy variation options theo variationIds
//        List<Long> variationIds = variations.stream().map(VariationEntity::getId).toList();
//        List<VariationOptionEntity> variationOptions = variationOptionRepo.findAllByVariationEntity_IdIn(variationIds);
//        variationOptions.forEach(VariationOptionEntity::softDelete);
//        variationOptionRepo.saveAll(variationOptions);
//
//        // 4. Lấy optionIds từ variationOptions
//        List<Long> optionIds = variationOptions.stream()
//                .map(vo -> vo.getOptionsEntity().getId())
//                .distinct()
//                .toList();
//        List<OptionsEntity> options = optionRepo.findAllByIdInAndDeletedAtIsNull(optionIds);
//        options.forEach(OptionsEntity::softDelete);
//        optionRepo.saveAll(options);
//
//        // 5. Lấy tierIds từ options
//        List<Long> tierIds = options.stream()
//                .map(o -> o.getTierEntity().getId())
//                .distinct()
//                .toList();
//        List<TierEntity> tiers = tierRepo.findAllByIdInAndDeletedAtIsNull(tierIds);
//        tiers.forEach(TierEntity::softDelete);
//        tierRepo.saveAll(tiers);

    }


    // ---------------- Private methods -----------------

    private ProductDto buildProductDto(ProductEntity product, Set<ProductDtofieldEnums> includeFields) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getTitle());

        List<VariationEntity> variants = Collections.emptyList();
        List<VariationOptionEntity> options = Collections.emptyList();

        // Variants
        if (includeFields.contains(ProductDtofieldEnums.VARIANTS) || includeFields.contains(ProductDtofieldEnums.OPTIONS)) {
            variants = variationRepo.findAllByProductEntity_IdAndDeletedAtIsNull(product.getId());
            options = variationOptionRepo.findAllByVariationEntity_IdIn(
                    variants.stream().map(VariationEntity::getId).toList()
            );
        }

        if (includeFields.contains(ProductDtofieldEnums.VARIANTS)) {
            dto.setVariations(buildVariants(variants, options));
        } else {
            dto.setVariations(Collections.emptyList());
        }

        if (includeFields.contains(ProductDtofieldEnums.OPTIONS)) {
            dto.setTiers(buildTiersDto(options));
        } else {
            dto.setTiers(Collections.emptyList());
        }

        if (includeFields.contains(ProductDtofieldEnums.CATEGORIES)) {
            dto.setCategories(buildCategories(product.getCategoryEntity().getId()));
        } else {
            dto.setCategories(Collections.emptyList());
        }

        return dto;
    }
    private List<CategoryDto> buildCategories(Long categoryId) {
    // Gọi service để lấy breadcrumb từ leaf → root
        return categoryServices.getBreadcrumb(categoryId);
    }
    private List<VariationDto> buildVariants(
            List<VariationEntity> variationEntities,
            List<VariationOptionEntity> variationOptionEntities
    ) {
        Map<Long, List<OptionDto>> optionsGrouped =
                ProductOptionUtils.groupOptions(variationOptionEntities, true);

        Map<Long, Map<Long, Integer>> optionsIndexMap =
                ProductOptionUtils.buildOptionsIndexMap(optionsGrouped);

        Map<Long, List<VariationOptionEntity>> variantOptionsMap = variationOptionEntities.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getVariationEntity().getId()
                ));

        return variationEntities.stream()
                .map(v -> {
                    VariationDto dto = new VariationDto();
                    dto.setId(v.getId());
                    dto.setSku(v.getSku());
                    dto.setPrice(v.getPrice());
                    dto.setStock(v.getStock());

                    List<VariationOptionEntity> variantOptions =
                            variantOptionsMap.getOrDefault(v.getId(), Collections.emptyList());

                    List<Integer> optionIndices = new ArrayList<>();
                    for (VariationOptionEntity vo : variantOptions) {
                        Long optionId = vo.getOptionsEntity().getTierEntity().getId();
                        Long valueId = vo.getOptionsEntity().getId();
                        Integer index = optionsIndexMap.get(optionId).get(valueId);
                        optionIndices.add(index);
                    }
                    dto.setOption_indices(optionIndices);

                    return dto;
                })
                .toList();
    }
    private List<TierDto> buildTiersDto(List<VariationOptionEntity> options) {
        return ProductOptionUtils.buildListTierDto(options);
    }
}
