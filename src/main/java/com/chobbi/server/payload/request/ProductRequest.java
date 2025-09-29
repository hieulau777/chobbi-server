package com.chobbi.server.payload.request;

import com.chobbi.server.dto.TierRequestDto;
import com.chobbi.server.dto.VariationRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    private Long id;
    private String title;
    private Long shopId;
    private Long categoryId;
    private List<VariationRequestDto> variations = new ArrayList<>();
    private List<TierRequestDto> tiers = new ArrayList<>();
}
