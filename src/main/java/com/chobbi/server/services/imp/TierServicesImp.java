package com.chobbi.server.services.imp;

import com.chobbi.server.dto.OptionRequestDto;
import com.chobbi.server.dto.TierDto;
import com.chobbi.server.dto.TierRequestDto;
import com.chobbi.server.entity.OptionsEntity;
import com.chobbi.server.entity.ProductEntity;
import com.chobbi.server.entity.TierEntity;
import com.chobbi.server.entity.VariationOptionEntity;
import com.chobbi.server.repo.OptionsRepo;
import com.chobbi.server.repo.TierRepo;
import com.chobbi.server.services.TierServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TierServicesImp implements TierServices {
    private final TierRepo tierRepo;
    private final OptionsRepo optionsRepo;

    @Override
    public List<TierDto> getTierDtoList(List<VariationOptionEntity> variationOptionEntities) {
        return List.of();
    }

    /**
     * Tạo tiers + options
     * @return list tiers, mỗi tier chứa list option (matrix)
     */
    @Override
    @Transactional
    public List<List<OptionsEntity>> createOrUpdateTiers(ProductEntity product, List<TierRequestDto> tierRequests) {
        List<List<OptionsEntity>> tierOptionsMatrix = new ArrayList<>();
        // Get current tiers and options of product
        List<TierEntity> existingTiers = tierRepo.findAllByProductEntity_IdAndDeletedAtIsNull(product.getId());
        Map<Long, TierEntity> existingTierMap = existingTiers.stream()
                 .collect(Collectors.toMap(TierEntity::getId, t -> t));
         Map<Long, OptionsEntity> existingOptionMap = existingTiers.stream()
                 .flatMap(t -> t.getOptionsEntities().stream())
                 .collect(Collectors.toMap(OptionsEntity::getId, o -> o));

        for (TierRequestDto t : tierRequests) {
            TierEntity tierEntity;
            if (t.getId() != null) {
                tierEntity = existingTierMap.remove(t.getId());
                if (tierEntity == null) throw new RuntimeException("Tier not found");
                tierEntity.setName(t.getName());
            } else {
                tierEntity = new TierEntity();
                tierEntity.setName(t.getName());
                tierEntity.setProductEntity(product);
                tierEntity = tierRepo.save(tierEntity);
            }

            List<OptionsEntity> options = new ArrayList<>();
            for (OptionRequestDto o : t.getOptions()) {
                OptionsEntity optEntity;
                if (o.getId() != null) {
                    optEntity = existingOptionMap.remove(o.getId());
                    if (optEntity == null) throw new RuntimeException("Option not found");
                    optEntity.setName(o.getName());
                } else {
                    optEntity = new OptionsEntity();
                    optEntity.setName(o.getName());
                    optEntity.setTierEntity(tierEntity);
                    optEntity = optionsRepo.save(optEntity);
                }
                options.add(optEntity);
            }
            tierEntity.setOptionsEntities(options);
            tierOptionsMatrix.add(options);
        }
        // Xoa cac tier va option khong co trong request
        for(TierEntity t : existingTierMap.values()) {
            t.softDelete();
            tierRepo.save(t);
        }
        for(OptionsEntity o : existingOptionMap.values()) {
            o.softDelete();
            optionsRepo.save(o);
        }
        return tierOptionsMatrix;
    }
}
