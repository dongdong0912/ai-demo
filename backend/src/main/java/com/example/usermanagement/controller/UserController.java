package com.example.usermanagement.controller;

import com.example.usermanagement.dao.UserDao;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.header:Authorization}")
    private String tokenHeader;

    public UserController(UserService userService, UserDao userDao, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 获取当前用户信息（个人中心）
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("message", "未登录或Token无效");
                return ResponseEntity.status(401).body(response);
            }

            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userDao.findByUsername(username).orElse(null);

            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.status(404).body(response);
            }

            // 不返回密码
            user.setPassword(null);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取个人资料失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取个人资料失败");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> params) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("message", "未登录或Token无效");
                return ResponseEntity.status(401).body(response);
            }

            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userDao.findByUsername(username).orElse(null);

            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.status(404).body(response);
            }

            // 更新可选字段
            if (params.containsKey("email")) {
                user.setEmail(params.get("email"));
            }
            if (params.containsKey("phone")) {
                user.setPhone(params.get("phone"));
            }
            if (params.containsKey("avatar")) {
                user.setAvatar(params.get("avatar"));
            }

            user = userDao.save(user);
            user.setPassword(null);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新个人资料失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "更新个人资料失败");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, String> params) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("message", "未登录或Token无效");
                return ResponseEntity.status(401).body(response);
            }

            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");

            if (oldPassword == null || newPassword == null || oldPassword.isEmpty() || newPassword.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "原密码和新密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            token = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            User user = userDao.findByUsername(username).orElse(null);

            if (user == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.status(404).body(response);
            }

            // 验证原密码
            if (user.getPassword() != null && !passwordEncoder.matches(oldPassword, user.getPassword())) {
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "原密码错误");
                return ResponseEntity.badRequest().body(response);
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userDao.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("修改密码失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "修改密码失败");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        log.debug("获取所有用户列表");
        List<User> users = userService.getAllUsers();
        log.info("成功获取用户列表, 共 {} 条数据", users.size());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", users);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        log.debug("根据ID获取用户: {}", id);
        return userService.getUserById(id)
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 200);
                    response.put("message", "success");
                    response.put("data", user);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.warn("用户不存在, ID: {}", id);
                    Map<String, Object> response = new HashMap<>();
                    response.put("code", 404);
                    response.put("message", "用户不存在");
                    return ResponseEntity.status(404).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user) {
        log.info("创建用户: {}", user.getUsername());
        try {
            User createdUser = userService.createUser(user);
            log.info("用户创建成功, ID: {}", createdUser.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 201);
            response.put("message", "创建成功");
            response.put("data", createdUser);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("用户创建失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("更新用户, ID: {}", id);
        try {
            User updatedUser = userService.updateUser(id, user);
            log.info("用户更新成功, ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("用户更新失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        log.info("删除用户, ID: {}", id);
        try {
            userService.deleteUser(id);
            log.info("用户删除成功, ID: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("用户删除失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("code", 404);
            response.put("message", e.getMessage());
            return ResponseEntity.status(404).body(response);
        }
    }
}
