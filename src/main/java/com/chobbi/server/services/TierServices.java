package com.chobbi.server.services;

import com.chobbi.server.dto.TierDto;
import com.chobbi.server.dto.TierRequestDto;
import com.chobbi.server.entity.OptionsEntity;
import com.chobbi.server.entity.ProductEntity;
import com.chobbi.server.entity.TierEntity;
import com.chobbi.server.entity.VariationOptionEntity;

import java.util.List;

public interface TierServices {
    List<TierDto> getTierDtoList(List<VariationOptionEntity> variationOptionEntities);
    List<List<OptionsEntity>> createOrUpdateTiers(ProductEntity product, List<TierRequestDto> tierRequests);
}
