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

        for (TierRequestDto t : tierRequests) {
            TierEntity tierEntity;
            if (t.getId() != null) {
                tierEntity = tierRepo.findById(t.getId())
                        .orElseThrow(() -> new RuntimeException("Tier not found"));
                tierEntity.setName(t.getName());
            } else {
                tierEntity = new TierEntity();
                tierEntity.setName(t.getName());
                tierEntity = tierRepo.save(tierEntity);
            }

            List<OptionsEntity> options = new ArrayList<>();
            for (OptionRequestDto o : t.getOptions()) {
                OptionsEntity optEntity;
                if (o.getId() != null) {
                    optEntity = optionsRepo.findById(o.getId())
                            .orElseThrow(() -> new RuntimeException("Option not found"));
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

        return tierOptionsMatrix;
    }
}
