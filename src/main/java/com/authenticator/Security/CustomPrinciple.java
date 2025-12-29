package com.authenticator.Security;


import lombok.RequiredArgsConstructor;


public class CustomPrinciple {
    public CustomPrinciple(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    private final String userid;
}
