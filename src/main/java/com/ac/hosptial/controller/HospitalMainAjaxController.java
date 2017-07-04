package com.ac.hosptial.controller;

import com.ac.common.ajax.AjaxResult;
import com.ac.common.constant.UserInfoConstant;
import com.ac.common.controller.AbstractAjaxController;
import com.ac.hosptial.model.MedicineDetailModel;
import com.ac.hosptial.service.HospitalFabricService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@RestController
@RequestMapping("/hospital/main")
public class HospitalMainAjaxController extends AbstractAjaxController {

    @Autowired
    private HospitalFabricService service;

    @GetMapping("/init")
    @ResponseBody
    private AjaxResult init() {

        List<MedicineDetailModel> medicineDetailList = Lists.newArrayList();

        MedicineDetailModel model = new MedicineDetailModel();
        model.setId("10001");
        model.setName("medicine10001");
        model.setPrice(100);
        medicineDetailList.add(model);

        model = new MedicineDetailModel();
        model.setId("10002");
        model.setName("medicine10002");
        model.setPrice(200);
        medicineDetailList.add(model);

        model = new MedicineDetailModel();
        model.setId("10003");
        model.setName("medicine10003");
        model.setPrice(300);
        medicineDetailList.add(model);

        AjaxResult result = AjaxResult.success();
        result.addData("medicineDetailList", medicineDetailList);
        return result;
    }

    @PostMapping("/upload")
    private AjaxResult save(@RequestBody List<MedicineDetailModel> medicineDetailList) {
        try {
            service.save(medicineDetailList, UserInfoConstant.USER_ID);
        } catch (Exception ex) {
            AjaxResult fail = AjaxResult.fail();
            fail.addData("msg", "Save Error!Pls try again!");
            return fail;
        }

        return AjaxResult.success();
    }

}
