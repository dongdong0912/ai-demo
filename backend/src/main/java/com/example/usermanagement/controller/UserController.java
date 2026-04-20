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
            @RequestBody Map<String, Object> params) {
        log.info("收到修改密码请求, params: {}", params);
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                log.warn("Token 为空或格式错误");
                Map<String, Object> response = new HashMap<>();
                response.put("code", 401);
                response.put("message", "未登录或Token无效");
                return ResponseEntity.status(401).body(response);
            }

            // 强制转换为字符串类型
            String oldPassword = String.valueOf(params.get("oldPassword"));
            String newPassword = String.valueOf(params.get("newPassword"));

            log.info("oldPassword: [{}], newPassword: [{}]", oldPassword, newPassword);

            // 处理 null 字符串的情况
            if ("null".equals(oldPassword) || "null".equals(newPassword) ||
                oldPassword.isEmpty() || newPassword.isEmpty()) {
                log.warn("密码参数为空");
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "原密码和新密码不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            String actualToken = token.substring(7);
            String username = jwtUtil.getUsernameFromToken(actualToken);
            log.info("Token 解析成功，用户名: {}", username);

            User user = userDao.findByUsername(username).orElse(null);

            if (user == null) {
                log.warn("用户不存在: {}", username);
                Map<String, Object> response = new HashMap<>();
                response.put("code", 404);
                response.put("message", "用户不存在");
                return ResponseEntity.status(404).body(response);
            }

            String storedPassword = user.getPassword();
            log.info("数据库存储的密码状态: {}", storedPassword == null ? "NULL" : "已加密(" + storedPassword.substring(0, Math.min(10, storedPassword.length())) + "...)");

            // 如果数据库密码为空（从未设置过），直接设置新密码
            if (storedPassword == null || storedPassword.isEmpty()) {
                log.info("用户密码为空，直接设置新密码");
                user.setPassword(passwordEncoder.encode(newPassword));
                userDao.save(user);

                Map<String, Object> response = new HashMap<>();
                response.put("code", 200);
                response.put("message", "密码设置成功");
                return ResponseEntity.ok(response);
            }

            // 验证原密码
            boolean matches = passwordEncoder.matches(oldPassword, storedPassword);
            log.info("原密码验证结果: {}", matches);

            if (!matches) {
                log.warn("原密码错误");
                Map<String, Object> response = new HashMap<>();
                response.put("code", 400);
                response.put("message", "原密码错误，请输入正确的原密码");
                return ResponseEntity.badRequest().body(response);
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            userDao.save(user);
            log.info("密码修改成功");

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "密码修改成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("修改密码失败: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "修改密码失败: " + e.getMessage());
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
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id) {
        log.debug("根据ID获取用户: {}", id);
        try {
            Long userId = Long.parseLong(id);
            return userService.getUserById(userId)
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
        } catch (NumberFormatException e) {
            log.warn("用户ID格式错误: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "用户ID无效");
            return ResponseEntity.badRequest().body(response);
        }
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
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody User user) {
        log.info("更新用户, ID: {}", id);
        try {
            // 验证 id 必须是数字
            Long userId = Long.parseLong(id);
            User updatedUser = userService.updateUser(userId, user);
            log.info("用户更新成功, ID: {}", userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedUser);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            log.warn("用户ID格式错误: {}", id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "用户ID无效");
            return ResponseEntity.badRequest().body(response);
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
