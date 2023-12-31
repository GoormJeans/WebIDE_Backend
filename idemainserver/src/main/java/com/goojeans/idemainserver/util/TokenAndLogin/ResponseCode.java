package com.goojeans.idemainserver.util.TokenAndLogin;

import jakarta.servlet.http.HttpServletResponse;


public enum ResponseCode {

    OK(HttpServletResponse.SC_OK,200,"REQUEST SUCCESS"),


    INVALID_TOKEN(HttpServletResponse.SC_BAD_REQUEST,4000,"Token Invalid."),

    // 회원 가입 관련 1
    ALEADY_EXIST_EMAIL(HttpServletResponse.SC_BAD_REQUEST, 4001, "Duplicate email account exists"),
    ALEADY_EXIST_NICKNAME(HttpServletResponse.SC_BAD_REQUEST, 4011, "Duplicate nickname account exists"),
    MISSING_VALUE(HttpServletResponse.SC_BAD_REQUEST,4111,"Missing value"),
    MISSING_REQUIRED_INFORMATION(HttpServletResponse.SC_BAD_REQUEST,4121,"Missing Required Information"),

    // 로그인 관련 2
    LOGIN_FAIL(HttpServletResponse.SC_BAD_REQUEST,4012,"Please recheck your password and email"),
    LOGIN_FAIL_SOCIAL(HttpServletResponse.SC_BAD_REQUEST,4022,"Please recheck your password and email"),

    // 마이페이지 수정 4
    EDIT_FAIL(HttpServletResponse.SC_BAD_REQUEST,4014,"failed Modify"),


    ENTITY_NOT_FOUND(4002, HttpServletResponse.SC_BAD_REQUEST,"Entity Not Found"),
    ACCESS_DENIED(4003,HttpServletResponse.SC_BAD_REQUEST,"Access is Denied"),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, 4004,"Not Found"),
    LOGIN_NEEDED(HttpServletResponse.SC_FORBIDDEN, 4005, "Login Required"),


    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 5000, "Server Error"),
    UNCATEGORIZED(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 5001, "Uncategorized");



    private final int code;
    private int status;
    private final String message;


    ResponseCode(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
