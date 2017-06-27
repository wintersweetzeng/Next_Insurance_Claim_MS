package com.ac.insurance.controller;

import com.ac.common.ajax.AjaxResult;
import com.ac.common.controller.AbstractAjaxController;
import com.ac.hosptial.model.MedicineDetailModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@Controller
@RequestMapping("/insurance")
public class InsuranceFabricAjaxController extends AbstractAjaxController {

    @PostMapping("/save")
    private AjaxResult save(@RequestBody List<MedicineDetailModel> medicineDetailList) {
        return AjaxResult.success();
    }



}
