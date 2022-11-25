package com.udangtangtang.shockshak.service;

import com.udangtangtang.shockshak.domain.ApplicationUser;
import com.udangtangtang.shockshak.dto.AuthDto;
import com.udangtangtang.shockshak.repository.ApplicationUserRepository;
import com.udangtangtang.shockshak.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(AuthDto authDto) throws IllegalStateException {
        if(validateDuplication(authDto.email())) throw new IllegalStateException("User trying to register with duplicated email");
        ApplicationUser applicationUser = new ApplicationUser(authDto.email(), passwordEncoder.encode(authDto.password()));
        userRepository.save(applicationUser);
    }

    @Override
    public AuthDto remove(AuthDto authDto) {
        return null;
    }

    @Override
    public AuthDto update(AuthDto authDto) {
        return null;
    }

    @Override
    public boolean validateDuplication(String username) {
        return userRepository.existsById(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApplicationUser applicationUser = userRepository.findById(username).orElseThrow();
        return new User(username, null, Collections.emptyList());
    }

}
