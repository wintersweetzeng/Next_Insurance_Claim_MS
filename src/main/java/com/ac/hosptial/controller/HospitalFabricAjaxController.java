package com.ac.hosptial.controller;

import com.ac.common.ajax.AjaxResult;
import com.ac.common.controller.AbstractAjaxController;
import com.ac.hosptial.model.MedicineDetailModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@RestController
@RequestMapping("/hospital")
public class HospitalFabricAjaxController extends AbstractAjaxController {

    @PostMapping("/expense")
    private AjaxResult save(@RequestBody List<MedicineDetailModel> medicineDetailList) {

        return AjaxResult.success();
    }


}
