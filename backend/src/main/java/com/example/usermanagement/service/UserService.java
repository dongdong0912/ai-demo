package com.example.usermanagement.service;

import com.example.usermanagement.entity.User;
import com.example.usermanagement.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        log.debug("Service: 查询所有用户");
        return userDao.findAll();
    }

    public Optional<User> getUserById(Long id) {
        log.debug("Service: 根据ID查询用户, id={}", id);
        return userDao.findById(id);
    }

    public User createUser(User user) {
        log.debug("Service: 创建用户, username={}", user.getUsername());
        if (userDao.existsByUsername(user.getUsername())) {
            log.warn("Service: 用户名已存在, username={}", user.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }
        return userDao.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        log.debug("Service: 更新用户, id={}", id);
        User user = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (!user.getUsername().equals(userDetails.getUsername())
                && userDao.existsByUsername(userDetails.getUsername())) {
            log.warn("Service: 用户名已存在, username={}", userDetails.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }

        user.setUsername(userDetails.getUsername());
        user.setRealName(userDetails.getRealName());
        user.setGender(userDetails.getGender());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setDepartment(userDetails.getDepartment());
        user.setRole(userDetails.getRole());
        user.setStatus(userDetails.getStatus());

        return userDao.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.debug("Service: 删除用户, id={}", id);
        if (!userDao.existsById(id)) {
            log.warn("Service: 用户不存在, id={}", id);
            throw new IllegalArgumentException("用户不存在");
        }
        userDao.deleteById(id);
    }
}
