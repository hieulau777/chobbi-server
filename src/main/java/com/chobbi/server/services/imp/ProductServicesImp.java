package com.chobbi.server.services.imp;

import com.chobbi.server.dto.*;
import com.chobbi.server.entity.*;
import com.chobbi.server.repo.*;
import com.chobbi.server.services.ProductServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServicesImp implements ProductServices {

    private final ProductRepo productRepo;
    private final ShopRepo shopRepo;
    private final ProductVariantRepo productVariantRepo;
    private final ProductVariantOptionRepo productVariantOptionRepo;

    @Override
    public ProductDto getProduct(Long shopId, Long productId) {
        // Check shop tồn tại
        ShopEntity shop = shopRepo.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        // Check product tồn tại
        ProductEntity product = productRepo.findByIdAndShopEntity_Id(productId, shop.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Lấy tất cả variant của product
        List<ProductVariantEntity> variantList = productVariantRepo.findAllByProductEntity_Id(product.getId());

        // Lấy tất cả option của các variant trong 1 query
        List<ProductVariantOptionEntity> optionList = productVariantOptionRepo
                .findAllByProductVariantEntity_IdIn(
                        variantList.stream().map(ProductVariantEntity::getId).toList()
                );

        // Map variantId -> list of optionValueIds
        Map<Long, List<Long>> variantOptionValueIds = optionList.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getProductVariantEntity().getId(),
                        Collectors.mapping(o -> o.getProductOptionValueEntity().getId(), Collectors.toList())
                ));

        // Map optionId -> list of ProductOptionValueDto (id + value)
        Map<Long, List<ProductOptionValueDto>> optionValuesGrouped = optionList.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getProductOptionValueEntity().getProductOptionEntity().getId(),
                        Collectors.mapping(
                                o -> new ProductOptionValueDto(
                                        o.getProductOptionValueEntity().getId(),
                                        o.getProductOptionValueEntity().getValue()
                                ),
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        list -> list.stream()
                                                .distinct()   // loại trùng
                                                .toList()
                                )
                        )
                ));

        // Map optionId -> optionName
        Map<Long, String> optionNames = optionList.stream()
                .map(o -> o.getProductOptionValueEntity().getProductOptionEntity())
                .distinct()
                .collect(Collectors.toMap(
                        ProductOptionEntity::getId,
                        ProductOptionEntity::getName
                ));

        // Map entity -> ProductVariantDto
        List<ProductVariantDto> variations = variantList.stream()
                .map(v -> {
                    ProductVariantDto dto = new ProductVariantDto();
                    dto.setId(v.getId());
                    dto.setSku(v.getSku());
                    dto.setPrice(v.getPrice());
                    dto.setStock(v.getStock());
                    dto.setOption_value_ids(variantOptionValueIds.getOrDefault(v.getId(), new ArrayList<>()));
                    return dto;
                }).toList();

        // Map options (Màu sắc, Kích thước, ...)
        List<ProductOptionDto> options = optionValuesGrouped.entrySet().stream()
                .map(e -> {
                    ProductOptionDto dto = new ProductOptionDto();
                    dto.setId(e.getKey());
                    dto.setName(optionNames.get(e.getKey()));
                    dto.setValues(e.getValue());
                    return dto;
                }).toList();

        // Build ProductDto
        ProductDto productDto = new ProductDto();
        productDto.setProduct_id(product.getId());
        productDto.setName(product.getTitle());
        productDto.setOptions(options);
        productDto.setVariations(variations);

        return productDto;
    }


}
