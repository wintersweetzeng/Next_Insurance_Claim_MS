package com.ac.hosptial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExpenseDetailModel {

    @JsonProperty("Uid")
    private String uid;

    /**
     * yyyyMMddHHmmss
     */
    @JsonProperty("ExpenseTime")
    private String expenseTime;

    @JsonProperty("Expense")
    private long expense;

    @JsonProperty("Claimed")
    private boolean claimed;

    @JsonProperty("ClaimExpense")
    private long claimExpense;

    @JsonProperty("Medicines")
    private List<MedicineDetailModel> medicines;


}
