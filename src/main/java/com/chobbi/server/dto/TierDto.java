package com.chobbi.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TierDto {
    private Long id;
    private String name;
    private List<OptionDto> options = new ArrayList<>();
}
