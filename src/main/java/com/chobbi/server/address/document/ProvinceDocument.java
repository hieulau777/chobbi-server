package com.chobbi.server.address.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "provinces")
public class ProvinceDocument {

    @Id
    private String id;

    @Field("code")
    private Integer code;

    @Field("name")
    private String name;

    @Field("division_type")
    private String divisionType;

    @Field("codename")
    private String codename;

    @Field("phone_code")
    private Integer phoneCode;

    @Field("districts")
    private List<DistrictDocument> districts;

    @Data
    public static class DistrictDocument {

        @Field("code")
        private Integer code;

        @Field("name")
        private String name;

        @Field("division_type")
        private String divisionType;

        @Field("codename")
        private String codename;

        @Field("province_code")
        private Integer provinceCode;

        @Field("wards")
        private List<WardDocument> wards;
    }

    @Data
    public static class WardDocument {

        @Field("code")
        private Integer code;

        @Field("name")
        private String name;

        @Field("division_type")
        private String divisionType;

        @Field("codename")
        private String codename;

        @Field("district_code")
        private Integer districtCode;
    }
}

