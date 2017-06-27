package com.ac.insurance.controller;

import com.ac.common.ajax.AjaxResult;
import com.ac.common.controller.AbstractAjaxController;
import com.ac.hosptial.model.LoginModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insurance")
public class InsuranceLoginAjaxController extends AbstractAjaxController {

    @PostMapping("/login")
    private AjaxResult login(@RequestBody LoginModel user) {

        if (StringUtils.equalsIgnoreCase("zhangsan", user.getUserName()) && StringUtils.equalsIgnoreCase(
                "123456", user.getPassword())) {
            AjaxResult result = AjaxResult.success();
            result.addData("redirectUrl", "/insurance/main");
            return result;
        }

        AjaxResult result = AjaxResult.fail();
        result.addData("errorMsg", "Failed to Login!");
        return result;
    }

}
