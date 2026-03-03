package com.chobbi.server.address.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProvinceDto {
    Integer code;
    String name;
    String divisionType;
    String codename;
    Integer phoneCode;
}

