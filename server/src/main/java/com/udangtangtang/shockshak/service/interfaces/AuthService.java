package com.udangtangtang.shockshak.service.interfaces;

import com.udangtangtang.shockshak.dto.AuthDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends CUDService<AuthDto>, UserDetailsService {
    boolean validateDuplication(String username);
}
