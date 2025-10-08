package com.chobbi.server.services.imp;

import com.chobbi.server.dto.VariationRequestDto;
import com.chobbi.server.entity.OptionsEntity;
import com.chobbi.server.entity.ProductEntity;
import com.chobbi.server.entity.VariationEntity;
import com.chobbi.server.entity.VariationOptionEntity;
import com.chobbi.server.repo.OptionsRepo;
import com.chobbi.server.repo.VariationOptionRepo;
import com.chobbi.server.repo.VariationRepo;
import com.chobbi.server.services.VariationServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VariationServicesImp implements VariationServices {

    private final VariationRepo variationRepo;
    private final VariationOptionRepo variationOptionRepo;
    private final OptionsRepo optionsRepo;

    /**
     * Tạo variation + variation_option
     * @param productEntity product parent
     * @param tierOptionsMatrix ma trận tiers->options
     */
    @Override
    @Transactional
    public void createVariations(ProductEntity productEntity, List<VariationRequestDto> variations,
                                 List<List<OptionsEntity>> tierOptionsMatrix) {

        Set<String> variationSignatures = new HashSet<>();

        for (VariationRequestDto variation : variations) {
            List<Integer> optionIndices = variation.getOption_indices();

            if (optionIndices.size() != tierOptionsMatrix.size()) {
                throw new RuntimeException("Variation option indices size mismatch with tiers");
            }

            String signature = optionIndices.stream().map(String::valueOf).collect(Collectors.joining("-"));
            if (variationSignatures.contains(signature)) {
                throw new RuntimeException("Duplicate variation combination: " + signature);
            }
            variationSignatures.add(signature);

            // Validate index
            for (int tierIdx = 0; tierIdx < optionIndices.size(); tierIdx++) {
                int optIdx = optionIndices.get(tierIdx);
                if (optIdx < 0 || optIdx >= tierOptionsMatrix.get(tierIdx).size()) {
                    throw new RuntimeException(
                            "Invalid option index at tier " + tierIdx + ": " + optIdx
                    );
                }
            }

            // Save variation
            VariationEntity variationEntity = new VariationEntity();
            variationEntity.setSku(variation.getSku());
            variationEntity.setPrice(variation.getPrice());
            variationEntity.setStock(variation.getStock());
            variationEntity.setProductEntity(productEntity);
            VariationEntity newVariation = variationRepo.save(variationEntity);

            // Save variation_option
            for (int tierIdx = 0; tierIdx < optionIndices.size(); tierIdx++) {
                int optIdx = optionIndices.get(tierIdx);
                OptionsEntity option = tierOptionsMatrix.get(tierIdx).get(optIdx);

                VariationOptionEntity vo = new VariationOptionEntity();
                vo.setVariationEntity(newVariation);
                vo.setOptionsEntity(option);
                variationOptionRepo.save(vo);
            }
        }
    }

    /**
     * Merge/update variations + variation_option
     */
    @Override
    @Transactional
    public void updateVariations(ProductEntity product, List<VariationRequestDto> variations, List<List<OptionsEntity>> tierOptionsMatrix) {
        // 1. Lấy tất cả variations hiện tại
        Map<Long, VariationEntity> currentVariations = product.getVariations()
                .stream().collect(Collectors.toMap(VariationEntity::getId, v -> v));

        Set<String> combinationSet = new HashSet<>();

        for (VariationRequestDto v : variations) {
            VariationEntity variationEntity;
            if (v.getId() != null) {
                // Cập nhật variation
                variationEntity = currentVariations.remove(v.getId());
                if (variationEntity == null) throw new RuntimeException("Variation not found");
                variationEntity.setSku(v.getSku());
                variationEntity.setPrice(v.getPrice());
                variationEntity.setStock(v.getStock());
                variationEntity = variationRepo.save(variationEntity);
            } else {
                // Tạo mới variation
                variationEntity = new VariationEntity();
                variationEntity.setSku(v.getSku());
                variationEntity.setPrice(v.getPrice());
                variationEntity.setStock(v.getStock());
                variationEntity.setProductEntity(product);
                variationEntity = variationRepo.save(variationEntity);
            }

            // Build variation_option mapping
            List<Integer> optionIndices = v.getOption_indices();
            if (optionIndices.size() != tierOptionsMatrix.size())
                throw new RuntimeException("Option indices size mismatch with tiers");

            StringBuilder keyBuilder = new StringBuilder();
            for (int tierIdx = 0; tierIdx < optionIndices.size(); tierIdx++) {
                int optIdx = optionIndices.get(tierIdx);
                List<OptionsEntity> optionsList = tierOptionsMatrix.get(tierIdx);
                if (optIdx < 0 || optIdx >= optionsList.size())
                    throw new RuntimeException("Invalid option index for tier " + tierIdx);
                keyBuilder.append(optIdx).append(",");
            }
            String combinationKey = keyBuilder.toString();
            if (combinationSet.contains(combinationKey))
                throw new RuntimeException("Duplicate variation combination");
            combinationSet.add(combinationKey);

            // Xóa variation_option cũ
            List<VariationOptionEntity> existingVOs = variationOptionRepo.findByVariationEntity_Id(variationEntity.getId());
            for (VariationOptionEntity vo : existingVOs) {
                variationOptionRepo.deleteById(vo.getId());
            }

            // Tạo mới variation_option
            for (int tierIdx = 0; tierIdx < optionIndices.size(); tierIdx++) {
                int optIdx = optionIndices.get(tierIdx);
                OptionsEntity option = tierOptionsMatrix.get(tierIdx).get(optIdx);
                VariationOptionEntity vo = new VariationOptionEntity();
                vo.setVariationEntity(variationEntity);
                vo.setOptionsEntity(option);
                variationOptionRepo.save(vo);
            }
        }

        // 2. Xóa variations còn lại (không có trong request)
        for (VariationEntity v : currentVariations.values()) {
            List<VariationOptionEntity> vos = variationOptionRepo.findByVariationEntity_Id(v.getId());
            for (VariationOptionEntity vo : vos) {
                variationOptionRepo.deleteById(vo.getId());
            }
            v.softDelete();
            variationRepo.save(v);
        }

    }

    @Override
    public List<VariationEntity> softDeleteByProductId(Long productId) {
        List<VariationEntity> variations = variationRepo.findAllByProductEntity_IdAndDeletedAtIsNull(productId);
        List<VariationOptionEntity> variationOptions = variationOptionRepo.findAllByVariationEntity_IdIn(
                variations.stream().map(VariationEntity::getId).toList()
        );
        List<OptionsEntity> options = variationOptions.stream().map(VariationOptionEntity::getOptionsEntity).toList();
        for (VariationEntity v : variations) {
            v.softDelete();
            variationRepo.save(v);
            for (VariationOptionEntity vo : variationOptions) {
                if (vo.getVariationEntity().getId().equals(v.getId())) {
                    vo.softDelete();
                    variationOptionRepo.save(vo);
                }
            }
        }
        for(OptionsEntity o : options) {
            o.softDelete();
            optionsRepo.save(o);
        }
        return List.of();
    }
}
