package com.ac.hosptial.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@Data
public class MedicineDetailModel {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Price")
    private int price;

    @JsonProperty("Number")
    private int number;
}
