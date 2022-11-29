package com.udangtangtang.shockshack.service.interfaces;

import com.udangtangtang.shockshack.dto.AuthDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends CUDService<AuthDto>, UserDetailsService {
    boolean validateDuplication(String username);
}
