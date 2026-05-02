package com.hopeonllc.askmom_user_service.service;

import com.hopeonllc.askmom_user_service.exception.DataValidationException;
import com.hopeonllc.askmom_user_service.exception.ResourceNotFoundException;
import com.hopeonllc.askmom_user_service.exception.UserAlreadyExistsException;
import com.hopeonllc.askmom_user_service.mapper.UserMapper;
import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User addNewUser(User requestUser) {
        validateEmailForCreate(requestUser.getEmail());
        User user = new User();
        userMapper.updateModel(requestUser, user);
        user.setPasswordHash(normalizePasswordHash(user.getPasswordHash()));
        User savedUser = userRepository.save(user);
        log.info("User created: id={}, email={}", savedUser.getId(), savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User updateUser(Long id, User requestUser) {
        // findById 返回的是 Optional<User>
        // orElseThrow(...)：是“取值”；有值就返回，没有就抛异常。
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        validateEmailForUpdate(id, requestUser.getEmail());
        userMapper.updateModel(requestUser, existingUser);
        existingUser.setPasswordHash(normalizePasswordHash(existingUser.getPasswordHash()));
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated: id={}, email={}", updatedUser.getId(), updatedUser.getEmail());
        return updatedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted: id={}", id);
    }

    private void validateEmailForCreate(String email) {
        if (email == null || email.isBlank()) {
            log.warn("Create user validation failed: email is empty");
            throw new DataValidationException("Email cannot be empty");
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("Create user validation failed: email already exists, email={}", email);
            throw new UserAlreadyExistsException("Email already exists: " + email);
        }
    }

    private void validateEmailForUpdate(Long id, String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        // findByEmail 返回的是 Optional<User>
        // ifPresent(...) 不是直接“取出来赋值”，是“有值才执行一段代码”
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.getId().equals(id)) {
                log.warn("Update user validation failed: email already exists, id={}, email={}", id, email);
                throw new UserAlreadyExistsException("Email already exists: " + email);
            }
        });
    }

    private String normalizePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DataValidationException("Password hash cannot be empty");
        }
        if (passwordHash.startsWith("$2a$") || passwordHash.startsWith("$2b$") || passwordHash.startsWith("$2y$")) {
            return passwordHash;
        }
        return passwordEncoder.encode(passwordHash);
    }
}
