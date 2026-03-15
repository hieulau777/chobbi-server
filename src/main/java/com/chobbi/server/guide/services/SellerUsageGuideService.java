package com.chobbi.server.guide.services;

import com.chobbi.server.guide.dto.SellerUsageGuideDto;

public interface SellerUsageGuideService {

    SellerUsageGuideDto getSellerGuide();

    SellerUsageGuideDto upsertSellerGuide(SellerUsageGuideDto dto);
}

