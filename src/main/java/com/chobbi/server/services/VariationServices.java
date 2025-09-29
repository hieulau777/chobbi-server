package com.chobbi.server.services;

import com.chobbi.server.dto.VariationDto;
import com.chobbi.server.dto.VariationRequestDto;
import com.chobbi.server.entity.OptionsEntity;
import com.chobbi.server.entity.ProductEntity;
import com.chobbi.server.entity.VariationEntity;
import com.chobbi.server.entity.VariationOptionEntity;

import java.util.List;

public interface VariationServices {
    void createVariations(ProductEntity productEntity, List<VariationRequestDto> variations,
                          List<List<OptionsEntity>> tierOptionsMatrix);
    void updateVariations(ProductEntity product, List<VariationRequestDto> variations,
                          List<List<OptionsEntity>> tierOptionsMatrix);
}
