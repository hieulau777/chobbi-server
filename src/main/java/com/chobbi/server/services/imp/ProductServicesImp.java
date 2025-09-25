package com.chobbi.server.services.imp;

import com.chobbi.server.dto.*;
import com.chobbi.server.entity.*;
import com.chobbi.server.enums.ProductDtofieldEnums;
import com.chobbi.server.repo.*;
import com.chobbi.server.services.CategoryServices;
import com.chobbi.server.services.ProductServices;
import com.chobbi.server.utils.ProductOptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public ProductDto getProduct(Long shopId, Long productId) {
        // Check shop tồn tại
        ShopEntity shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // Check product tồn tại
        ProductEntity product = productRepo.findByIdAndShopEntity_Id(productId, shop.getId())
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

        List<ProductEntity> productList = productRepo.findAllByShopEntity_Id(shop.getId());

        return productList.stream()
                .map(product -> buildProductDto(product, Set.of(
                        ProductDtofieldEnums.VARIANTS,
                        ProductDtofieldEnums.OPTIONS,
                        ProductDtofieldEnums.CATEGORIES
                )))
                .toList();
    }



    // ---------------- Private methods -----------------

    private ProductDto buildProductDto(ProductEntity product, Set<ProductDtofieldEnums> includeFields) {
        ProductDto dto = new ProductDto();
        dto.setProduct_id(product.getId());
        dto.setName(product.getTitle());

        List<VariationEntity> variants = Collections.emptyList();
        List<VariationOptionEntity> options = Collections.emptyList();

        // Variants
        if (includeFields.contains(ProductDtofieldEnums.VARIANTS) || includeFields.contains(ProductDtofieldEnums.OPTIONS)) {
            variants = variationRepo.findAllByProductEntity_Id(product.getId());
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
            dto.setOptions(buildOptions(options));
        } else {
            dto.setOptions(Collections.emptyList());
        }

        if (includeFields.contains(ProductDtofieldEnums.CATEGORIES)) {
            dto.setCategories(buildCategories(product.getCategoryEntity().getId()));
        } else {
            dto.setCategories(Collections.emptyList());
        }

        return dto;
    }
    private List<ProductCategoryDto> buildCategories(Long categoryId) {
    // Gọi service để lấy breadcrumb từ leaf → root
    return categoryServices.getBreadcrumb(categoryId);
}
    private List<ProductVariantDto> buildVariants(
            List<VariationEntity> variants,
            List<VariationOptionEntity> options
    ) {
        Map<Long, List<ProductOptionValueDto>> optionsGrouped =
                ProductOptionUtils.groupOptions(options, true);

        Map<Long, Map<Long, Integer>> optionsIndexMap =
                ProductOptionUtils.buildOptionsIndexMap(optionsGrouped);

        Map<Long, List<VariationOptionEntity>> variantOptionsMap = options.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getVariationEntity().getId()
                ));

        return variants.stream()
                .map(v -> {
                    ProductVariantDto dto = new ProductVariantDto();
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
                    dto.setOptionIndex(optionIndices);

                    return dto;
                })
                .toList();
    }
    private List<ProductOptionDto> buildOptions(List<VariationOptionEntity> options) {
        return ProductOptionUtils.buildOptionDtos(options);
    }
}
