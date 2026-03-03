package com.chobbi.server.address.controller;

import com.chobbi.server.address.dto.DistrictDto;
import com.chobbi.server.address.dto.ProvinceDto;
import com.chobbi.server.address.dto.WardDto;
import com.chobbi.server.address.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/provinces")
    public ResponseEntity<List<ProvinceDto>> getProvinces() {
        List<ProvinceDto> provinces = addressService.getAllProvinces();
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/provinces/{provinceCode}/districts")
    public ResponseEntity<List<DistrictDto>> getDistrictsByProvince(
            @PathVariable("provinceCode") Integer provinceCode
    ) {
        List<DistrictDto> districts = addressService.getDistrictsByProvinceCode(provinceCode);
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/districts/{districtCode}/wards")
    public ResponseEntity<List<WardDto>> getWardsByDistrict(
            @PathVariable("districtCode") Integer districtCode
    ) {
        List<WardDto> wards = addressService.getWardsByDistrictCode(districtCode);
        return ResponseEntity.ok(wards);
    }
}

