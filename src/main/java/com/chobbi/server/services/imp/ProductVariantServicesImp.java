//package com.chobbi.server.services.imp;
//
//import com.chobbi.server.dto.ProductVariantDto;
//import com.chobbi.server.entity.ProductVariantEntity;
//import com.chobbi.server.entity.ProductVariantOptionEntity;
//import com.chobbi.server.services.ProductVariantServices;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ProductVariantServicesImp implements ProductVariantServices {
//
//    @Override
//    public List<ProductVariantDto> getProductVariants(List<ProductVariantEntity> variants, List<ProductVariantOptionEntity> options) {
//        Map<Long, List<ProductOptionValueDto>> optionValuesGrouped =
//                ProductOptionUtils.groupOptionValues(options, true);
//
//        Map<Long, Map<Long, Integer>> optionValueIndexMap =
//                ProductOptionUtils.buildOptionValueIndexMap(optionValuesGrouped);
//
//        Map<Long, List<ProductVariantOptionEntity>> variantOptionsMap = options.stream()
//                .collect(Collectors.groupingBy(
//                        o -> o.getProductVariantEntity().getId()
//                ));
//
//        return variants.stream()
//                .map(v -> {
//                    ProductVariantDto dto = new ProductVariantDto();
//                    dto.setId(v.getId());
//                    dto.setSku(v.getSku());
//                    dto.setPrice(v.getPrice());
//                    dto.setStock(v.getStock());
//
//                    List<ProductVariantOptionEntity> variantOptions =
//                            variantOptionsMap.getOrDefault(v.getId(), Collections.emptyList());
//
//                    List<Integer> optionIndices = new ArrayList<>();
//                    for (ProductVariantOptionEntity vo : variantOptions) {
//                        Long optionId = vo.getProductOptionValueEntity()
//                                .getProductOptionEntity().getId();
//                        Long valueId = vo.getProductOptionValueEntity().getId();
//                        Integer index = optionValueIndexMap.get(optionId).get(valueId);
//                        optionIndices.add(index);
//                    }
//                    dto.setOptionIndex(optionIndices);
//
//                    return dto;
//                })
//                .toList();
//    }
//}
