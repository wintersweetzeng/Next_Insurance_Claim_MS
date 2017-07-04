package com.ac.insurance.controller;

import com.ac.common.ajax.AjaxResult;
import com.ac.common.constant.UserInfoConstant;
import com.ac.common.controller.AbstractAjaxController;
import com.ac.hosptial.model.ExpenseDetailModel;
import com.ac.hosptial.service.HospitalFabricService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@RestController
@RequestMapping("/insurance/main")
public class InsuranceMainAjaxController extends AbstractAjaxController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HospitalFabricService hospitalFabricService;


    @GetMapping("/load")
    private AjaxResult load() {

        String medResultJson = hospitalFabricService.query(UserInfoConstant.USER_ID);
        if (StringUtils.isNotBlank(medResultJson)) {

            try {
                List<ExpenseDetailModel> result = objectMapper.readValue(medResultJson, objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ExpenseDetailModel.class));

                result.forEach( detail -> {
                    long[] expense = {0};
                    detail.getMedicines().forEach(med -> {
                        expense[0] += med.getNumber() * med.getPrice();
                    });

                    detail.setExpense(expense[0]);
                });

                //todo:  data validate check
                //XXXXXX

                AjaxResult ajaxResult = AjaxResult.success();
                ajaxResult.addData("json", objectMapper.writeValueAsString(result));
                return ajaxResult;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            AjaxResult ajaxResult = AjaxResult.fail();
            ajaxResult.addData("msg", "no data.");
            return ajaxResult;
        }


        return AjaxResult.success();
    }

    @GetMapping("/claim")
    private AjaxResult claim(String expenseTime) throws Exception {

        try {
            hospitalFabricService.claim(UserInfoConstant.USER_ID, expenseTime);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return AjaxResult.success();
    }

}
