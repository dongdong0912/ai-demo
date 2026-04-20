package com.example.usermanagement.service;

import com.example.usermanagement.dao.UserDao;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户登录
     */
    @Transactional
    public Map<String, Object> login(String username, String password) {
        User user = userDao.findByUsername(username).orElse(null);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查密码是否为空
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            // 首次登录：设置默认密码 123456 并加密存储
            log.info("用户 {} 密码为空，自动设置默认密码", username);
            user.setPassword(passwordEncoder.encode("123456"));
            if (user.getRole() == null) {
                user.setRole("USER");
            }
            user = userDao.save(user);
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 更新登录统计
        user.setLastLoginTime(java.time.LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() == null ? 1 : user.getLoginCount() + 1);
        userDao.save(user);

        String token = jwtUtil.generateToken(username, user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("id", user.getId());

        return result;
    }

    /**
     * 用户注册
     */
    public User register(String username, String password) {
        if (userDao.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setStatus(1);

        return userDao.save(user);
    }

    /**
     * 获取用户信息（根据 Token）
     */
    public Map<String, Object> getUserInfo(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userDao.findByUsername(username).orElse(null);

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("email", user.getEmail());
        result.put("phone", user.getPhone());

        return result;
    }
}
