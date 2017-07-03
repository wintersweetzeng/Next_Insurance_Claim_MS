package com.ac.hosptial.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
@Controller
@RequestMapping("/hospital")
public class HospitalViewController {

    @RequestMapping(value = {"/init",""})
    private String login() {
        return "hospital/login";
    }

    @RequestMapping("/main")
    private String main() {
        return "hospital/main";
    }
}
