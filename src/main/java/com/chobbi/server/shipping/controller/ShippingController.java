package com.chobbi.server.shipping.controller;

import com.chobbi.server.shipping.dto.ShippingOptionDto;
import com.chobbi.server.shipping.services.ShippingEstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingEstimateService shippingEstimateService;

    /**
     * Ước tính phí giao hàng theo từng phương thức khi biết tổng trọng lượng (gram).
     */
    @GetMapping("/estimate")
    public ResponseEntity<List<ShippingOptionDto>> estimateByWeight(
            @RequestParam(name = "weightGram", defaultValue = "0") long weightGram) {
        List<ShippingOptionDto> options = shippingEstimateService.estimateByWeight(weightGram);
        return ResponseEntity.ok(options);
    }
}
