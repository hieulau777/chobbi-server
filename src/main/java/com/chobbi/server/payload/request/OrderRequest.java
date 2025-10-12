package com.chobbi.server.payload.request;

import com.chobbi.server.dto.OrderProductRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Long accountId;
    private List<OrderProductRequestDto> orders;
}
