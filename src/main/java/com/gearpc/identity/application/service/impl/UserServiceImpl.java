package com.gearpc.identity.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.identity.application.dto.UserDetailResponse;
import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.AdminUserResponse;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;
import com.gearpc.identity.application.mapper.UserMapper;
import com.gearpc.identity.application.service.UserService;
import com.gearpc.identity.domain.entity.Role;
import com.gearpc.identity.domain.entity.User;
import com.gearpc.identity.domain.valueobject.enums.RoleType;
import com.gearpc.identity.domain.valueobject.enums.UserStatus;
import com.gearpc.identity.repository.RoleRepository;
import com.gearpc.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
//    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

//    @Override
//    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {
//        User user = userMapper.toUser(registerUserRequest);
//        user.setPasswordHash(passwordEncoder.encode(registerUserRequest.password()));
//        Role role = roleRepository.findByName(RoleType.CUSTOMER)
//                .orElseGet(() -> roleRepository.save(
//                        new Role(RoleType.CUSTOMER, "Vai trò cho người dùng")));
//        user.assignRole(role);
//        try {
//            return userMapper.toRegisterUserResponse(userRepository.save(user));
//        } catch (DataIntegrityViolationException e) {
//            log.error("Vi phạm tính toàn vẹn dữ liệu khi đăng ký user: {}", e.getMessage());
//            throw new AppException(ErrorCode.USER_EXISTS);
//        }
//    }

    @Override
    public UserDetailResponse getMyProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserDetailResponse(user);
    }

    @Override
    public List<AdminUserResponse> getAllUser() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toAdminUserResponse)
                .toList();
    }

    @Override
    public UserDetailResponse getMyProfileByKeycloakId(UUID keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserDetailResponse(user);
    }

    @Override
    @Transactional
    public UserDetailResponse getOrCreateProfile(Jwt jwt, Authentication authentication) {
        UUID keycloakId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> createUserFromKeycloak(jwt, keycloakId));

        if (Boolean.TRUE.equals(jwt.getClaim("email_verified"))
                && user.getUserStatus() == com.gearpc.identity.domain.valueobject.enums.UserStatus.PENDING_VERIFICATION) {
            user.setEmailVerified(true);
            user.setUserStatus(com.gearpc.identity.domain.valueobject.enums.UserStatus.ACTIVE);
            userRepository.save(user);
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replaceFirst("^ROLE_", ""))
                .filter(role -> Arrays.stream(RoleType.values())
                        .map(Enum::name)
                        .anyMatch(role::equals))
                .collect(Collectors.toSet());

        return UserDetailResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .phone(user.getPhone())
                .avatarKey(user.getAvatarKey())
                .userStatus(user.getUserStatus())
                .emailVerified(user.isEmailVerified())
                .roles(roles)
                .build();
    }

    private User createUserFromKeycloak(Jwt jwt, UUID keycloakId) {
        String email = firstNonBlank(
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("preferred_username"),
                keycloakId.toString()
        );

        String firstName = firstNonBlank(
                jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("preferred_username"),
                "User"
        );

        String lastName = firstNonBlank(
                jwt.getClaimAsString("family_name"),
                ""
        );

        User user = User.builder()
                .keycloakId(keycloakId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .emailVerified(Boolean.TRUE.equals(jwt.getClaim("email_verified")))
                .userStatus(Boolean.TRUE.equals(jwt.getClaim("email_verified"))
                        ? UserStatus.ACTIVE
                        : UserStatus.PENDING_VERIFICATION)
                .build();

        return userRepository.save(user);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
