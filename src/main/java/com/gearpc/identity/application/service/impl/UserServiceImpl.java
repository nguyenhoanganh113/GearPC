package com.gearpc.identity.application.service.impl;

import com.gearpc.common.exception.AppException;
import com.gearpc.common.exception.ErrorCode;
import com.gearpc.identity.application.dto.request.RegisterUserRequest;
import com.gearpc.identity.application.dto.response.RegisterUserResponse;
import com.gearpc.identity.application.mapper.UserMapper;
import com.gearpc.identity.application.service.UserService;
import com.gearpc.identity.domain.entity.Role;
import com.gearpc.identity.domain.entity.User;
import com.gearpc.identity.domain.valueobject.enums.RoleType;
import com.gearpc.identity.repository.RoleRepository;
import com.gearpc.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public RegisterUserResponse registerUser(RegisterUserRequest registerUserRequest) {

        // 1. Convert DTO sang Entity
        User user = userMapper.toUser(registerUserRequest);

        // 2. Mã hóa password
        user.setPasswordHash(passwordEncoder.encode(registerUserRequest.password()));

        // 3. Lấy role CUSTOMER nếu chưa có thì tạo
        Role role = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.CUSTOMER, "Vai trò cho người dùng")));

        // 4. Gán role cho user
        user.assignRole(role);

        // 5. Lưu user vào database
        try {
            // Lưu user vào database
            User savedUser = userRepository.save(user);

            // 6. Trả về response
            return userMapper.toRegisterUserResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            log.error(
                    "Vi phạm tính toàn vẹn dữ liệu trong quá trình đăng ký người dùng.: {}",
                    e.getMessage()
            );
            throw new AppException(ErrorCode.USER_EXISTS);
        }
    }
}
