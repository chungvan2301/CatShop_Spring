package com.example.Cat.Shop.dto;

import com.example.Cat.Shop.model.User;
import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private Integer userId;
    private String nameReceiver;
    private String province;
    private String district;
    private String ward;
    private String provinceId;
    private String districtId;
    private String wardId;
    private String streetAndDepartment;
    private String phoneNumber;
    private String type;
    private Boolean addressDefault;

}
