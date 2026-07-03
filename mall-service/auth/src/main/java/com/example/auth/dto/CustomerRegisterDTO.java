package com.example.auth.dto;

import lombok.Data;

@Data
public class CustomerRegisterDTO {
    private String phone;
    private String password;
    private String nickname;
}
