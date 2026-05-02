package com.hopeonllc.askmom_user_service.controller;

import com.hopeonllc.askmom_user_service.mapper.UserMapper;
import com.hopeonllc.askmom_user_service.model.User;
import com.hopeonllc.askmom_user_service.request.UserRequest;
import com.hopeonllc.askmom_user_service.response.UserExistsResponse;
import com.hopeonllc.askmom_user_service.response.UserResponse;
import com.hopeonllc.askmom_user_service.service.IUserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor // 不用写构造函数
@RequestMapping("/user") // 告诉 Spring：这个 Controller 负责处理 /user 路径的请求
@Validated
public class UserController {

    private final IUserService userService;
    private final UserMapper userMapper;

    /**
     * PostMapping，当客户端发送 POST 请求到 /add/new-user 这个地址时，执行下面这个方法
     * RequestBody，Spring 去读取 HTTP Body 内容，然后把 JSON 反序列化为 Java 对象。
     *
     * 接受前端的信息，信息请求格式如下
     * fetch("/user", {
     *   method: "POST",
     *   headers: {
     *     "Content-Type": "application/json"
     *   },
     *   body: JSON.stringify({
     *     name: "Pan",
     *     email: "pan@gmail.com"
     *   })
     * })
     *
     * @param request 请求体中的用户信息
     * @return 返回包含新建用户结果的 ResponseEntity 响应对象
     */
    @PostMapping("/add/new-user")
    public ResponseEntity<UserResponse> addNewUser(@Valid @RequestBody UserRequest request) {
        User savedUser = userService.addNewUser(userMapper.toModel(request));

        // ok(...) 是快捷写法，固定状态码 200 OK，用的是 ResponseEntity 的静态工厂方法
        // 如果不用 ok，就需要手动创建，自己明确传 body + HttpStatus
        // new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.ok(userMapper.toResponse(savedUser));
    }

    // @PathVariable Long id，从 URL 路径中取出变量值，并赋给方法参数 id
    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        User updatedUser = userService.updateUser(id, userMapper.toModel(request));
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> responses = userMapper.toResponses(userService.getAllUsers());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toResponse(userService.getUserById(id)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable @NotBlank @Email String email) {
        return ResponseEntity.ok(userMapper.toResponse(userService.getUserByEmail(email)));
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<UserExistsResponse> existsUserByEmail(@PathVariable @NotBlank @Email String email) {
        return ResponseEntity.ok(new UserExistsResponse(String.valueOf(userService.existsUserByEmail(email))));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
