package com.ac.hosptial.model;

import lombok.Data;

import java.util.List;

@Data
public class ExpenseDetailModel {

    private String uid;

    /**
     * yyyyMMddHHmmss
     */
    private String expenseTime;

    private boolean claimed;

    private List<MedicineDetailModel> medicines;


}
