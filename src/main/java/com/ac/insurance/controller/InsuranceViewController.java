package com.ac.insurance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@Controller
@RequestMapping("/insurance")
public class InsuranceViewController {

    @RequestMapping(value = {"/init", ""})
    private String login() {
        return "insurance/login";
    }

    @RequestMapping("/main")
    private String main() {
        return "insurance/main";
    }


}
