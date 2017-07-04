package com.ac.hosptial.service;

import java.util.List;

import com.ac.hosptial.model.MedicineDetailModel;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
public interface HospitalFabricService {

    void save(List<MedicineDetailModel> medicineDetailList, String userId) throws Exception;

    String query(String userId);

    void claim(String userId, String expenseTime) throws Exception;
}
