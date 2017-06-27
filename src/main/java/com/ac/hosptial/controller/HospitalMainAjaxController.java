package com.ac.hosptial.controller;

import com.ac.common.ajax.AjaxResult;
import com.ac.common.controller.AbstractAjaxController;
import com.ac.hosptial.model.MedicineDetailModel;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@RestController
@RequestMapping("/hospital")
public class HospitalMainAjaxController extends AbstractAjaxController {

    @GetMapping("/main/init")
    @ResponseBody
    private AjaxResult main() {

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

}
