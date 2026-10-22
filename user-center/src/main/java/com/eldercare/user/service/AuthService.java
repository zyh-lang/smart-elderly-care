package com.eldercare.user.service;

import com.eldercare.user.dto.LoginDTO;
import com.eldercare.user.dto.LoginVO;
import com.eldercare.user.dto.RegisterDTO;

/**
 * 认证服务。
 */
public interface AuthService {

    LoginVO login(LoginDTO dto);

    void register(RegisterDTO dto);

    void logout();
}
