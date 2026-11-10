package com.yuddy.luoning.security;

import com.yuddy.luoning.repository.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.oauth2.jwt.Jwt;

// Chay sau khi token da dung chu ky va con han: hoi DB xem user co bi khoa khong
public class LockedUserCheckingConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Converter<Jwt, AbstractAuthenticationToken> delegate;
    private final UserRepository userRepository;

    public LockedUserCheckingConverter(Converter<Jwt, AbstractAuthenticationToken> delegate,
            UserRepository userRepository) {
        this.delegate = delegate;
        this.userRepository = userRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        if (userRepository.existsByIdAndLockedTrue(CurrentUser.id(jwt))) {
            // LockedException la AuthenticationException nen Spring tra ve 401
            throw new LockedException("Tai khoan da bi khoa");
        }
        return delegate.convert(jwt);
    }
}
