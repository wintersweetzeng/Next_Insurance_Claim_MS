package com.ac.hosptial.service;

import com.ac.hosptial.model.MedicineDetailModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;

import java.util.List;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
public interface HospitalFabricService {

    void save(List<MedicineDetailModel> medicineDetailList, String userId) throws Exception;
}
