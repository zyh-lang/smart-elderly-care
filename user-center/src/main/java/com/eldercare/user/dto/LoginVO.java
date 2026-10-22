package com.eldercare.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录返回结果。
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;

    private Long userId;

    private String username;

    private String role;

    private String realName;
}
