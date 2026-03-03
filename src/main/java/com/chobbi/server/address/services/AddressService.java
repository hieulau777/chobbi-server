package com.chobbi.server.address.services;

import com.chobbi.server.address.dto.DistrictDto;
import com.chobbi.server.address.dto.ProvinceDto;
import com.chobbi.server.address.dto.WardDto;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final MongoTemplate mongoTemplate;

    /**
     * GET /address/provinces
     * Lấy danh sách tỉnh/thành từ collection diachi.
     */
    public List<ProvinceDto> getAllProvinces() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.project()
                        .and("code").as("code")
                        .and("name").as("name")
                        .and("division_type").as("division_type")
                        .and("codename").as("codename")
                        .and("phone_code").as("phone_code")
                        .andExclude("_id")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, "provinces", Document.class);

        List<Document> docs = results.getMappedResults();

        return docs.stream()
                .map(d -> ProvinceDto.builder()
                        .code(d.getInteger("code"))
                        .name(d.getString("name"))
                        .divisionType(d.getString("division_type"))
                        .codename(d.getString("codename"))
                        .phoneCode(d.getInteger("phone_code"))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * GET /address/provinces/{provinceCode}/districts
     * Lấy danh sách quận/huyện theo code của tỉnh.
     */
    public List<DistrictDto> getDistrictsByProvinceCode(Integer provinceCode) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("code").is(provinceCode)),
                Aggregation.unwind("districts"),
                Aggregation.project()
                        .and("districts.code").as("code")
                        .and("districts.name").as("name")
                        .and("districts.division_type").as("division_type")
                        .and("districts.codename").as("codename")
                        .and("districts.province_code").as("province_code")
                        .andExclude("_id")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, "provinces", Document.class);

        List<Document> docs = results.getMappedResults();
        if (docs.isEmpty()) {
            return Collections.emptyList();
        }

        return docs.stream()
                .map(d -> DistrictDto.builder()
                        .code(d.getInteger("code"))
                        .name(d.getString("name"))
                        .divisionType(d.getString("division_type"))
                        .codename(d.getString("codename"))
                        .provinceCode(d.getInteger("province_code"))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * GET /address/districts/{districtCode}/wards
     * Lấy danh sách phường/xã theo code của quận.
     */
    public List<WardDto> getWardsByDistrictCode(Integer districtCode) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("districts"),
                Aggregation.match(Criteria.where("districts.code").is(districtCode)),
                Aggregation.unwind("districts.wards"),
                Aggregation.project()
                        .and("districts.wards.code").as("code")
                        .and("districts.wards.name").as("name")
                        .and("districts.wards.division_type").as("division_type")
                        .and("districts.wards.codename").as("codename")
                        .and("districts.wards.district_code").as("district_code")
                        .andExclude("_id")
        );

        AggregationResults<Document> results =
                mongoTemplate.aggregate(agg, "provinces", Document.class);

        List<Document> docs = results.getMappedResults();
        if (docs.isEmpty()) {
            return Collections.emptyList();
        }

        return docs.stream()
                .map(w -> WardDto.builder()
                        .code(w.getInteger("code"))
                        .name(w.getString("name"))
                        .divisionType(w.getString("division_type"))
                        .codename(w.getString("codename"))
                        .districtCode(w.getInteger("district_code"))
                        .build())
                .collect(Collectors.toList());
    }
}