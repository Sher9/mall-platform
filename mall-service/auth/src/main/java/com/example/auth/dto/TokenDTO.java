package com.example.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TokenDTO {
    private String accessToken;
    private String tokenType;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String phone;
    private List<String> roles;
    private List<String> permissions;
}
