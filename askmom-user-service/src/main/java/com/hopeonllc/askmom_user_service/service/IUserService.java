package com.hopeonllc.askmom_user_service.service;

import com.hopeonllc.askmom_user_service.model.User;

import java.util.List;

public interface IUserService {

    User addNewUser(User requestUser);

    User updateUser(Long id, User requestUser);

    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByEmail(String email);

    boolean existsUserByEmail(String email);

    void deleteUser(Long id);
}
