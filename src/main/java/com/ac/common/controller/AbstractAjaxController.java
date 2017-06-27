package com.ac.common.controller;

import com.ac.common.ajax.AjaxResult;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhenchao.bi on 6/26/2017.
 */
public class AbstractAjaxController {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public AjaxResult exceptionHandler(Throwable throwable, NativeWebRequest webRequest) {
        return this.handleException(throwable, webRequest);
    }

    private AjaxResult handleException(Throwable throwable, NativeWebRequest webRequest) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String errorMessage = ExceptionUtils.getRootCauseMessage(throwable);
        String requestURI = request.getRequestURI();
        String message = throwable.getMessage();

        throwable.printStackTrace();

        AjaxResult result = AjaxResult.fail();
        result.addData("errorMessage", errorMessage)
                .addData("detailErrorMessage", message)
                .addData("referrer", request.getHeader("referer"))
                .addData("requestUri", requestURI);

        return result;
    }
}
