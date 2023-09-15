package com.susu.proxy.server.web.service;

import com.susu.proxy.core.common.LocalStorage;
import com.susu.proxy.core.common.utils.EncryptUtil;
import com.susu.proxy.core.common.utils.StringUtils;
import com.susu.proxy.core.config.AppConfig;
import com.susu.proxy.core.config.ServerConfig;
import com.susu.proxy.server.web.entity.UserInfo;
import com.susu.proxy.server.web.servlet.InstantiationComponent;
import com.susu.proxy.server.web.servlet.SysException;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AuthService implements InstantiationComponent {

    private final String path =  System.getProperty("user.dir") + File.separator + "data" + File.separator + "user.info";

    private final Map<String, UserInfo> users = new ConcurrentHashMap<>();

    public AuthService() {
        loadReadyUsers();
        Runtime.getRuntime().addShutdownHook(new Thread(this::loadWriteUsers));
        instantiationComponent();
    }

    public boolean isExist(String username) {
        return !StringUtils.isBlank(username) && users.containsKey(username);
    }

    public boolean verify(String username, String password) {

        if (!isExist(username)) {
            return false;
        }

        return EncryptUtil.decode(users.get(username).getPassword(), AppConfig.secretKey).equals(password);
    }

    public void changePassword(String username, String oldPassword, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(oldPassword) || StringUtils.isBlank(oldPassword)) {
            throw new SysException("用户名密码不能为空");
        }

        UserInfo user = users.get(username);

        if (user == null) {
            throw new SysException("用户不存在");
        }
        String decode = EncryptUtil.decode(user.getPassword(), AppConfig.secretKey);
        if (!oldPassword.equals(decode)) {
            throw new SysException("密码错误");
        }

        if (password.equals(decode)) {
            throw new SysException("新密码不能与旧密码相同");
        }

        user.setPassword(EncryptUtil.encode(password, AppConfig.secretKey));
        users.put(username, user);
        loadWriteUsers();
    }

    /**
     * 添加一个用户，如果已存在用户则为修改
     */
    public void addUser(UserInfo user) {
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            throw new SysException("用户名密码不能为空");
        }
        UserInfo containsUser = users.get(user.getUsername());
        if (containsUser != null) {
            throw new SysException("用户名已存在");
        }

        user.setPassword(EncryptUtil.encode(user.getPassword(), AppConfig.secretKey));

        users.put(user.getUsername(),user);
        loadWriteUsers();
    }

    /**
     * <p>Description: Delete user, delete user online token </p>
     * <p>Description: 删除用户，删除用户在线Token </p>
     *
     * @param username 用户名
     */
    public UserInfo deleteUser(String username) {
        UserInfo user = users.remove(username);
        loadWriteUsers();
        return user;
    }

    private void loadReadyUsers() {
        users.clear();
        List<UserInfo> usersJson = LocalStorage.loadReady(path, UserInfo.class);
        if (usersJson == null) {
            users.put(ServerConfig.username, new UserInfo(ServerConfig.username, EncryptUtil.encode(ServerConfig.password, AppConfig.secretKey)));
            return;
        }

        for (UserInfo user : usersJson) {
            users.put(user.getUsername(), user);
        }
    }

    private void loadWriteUsers() {
        log.info("Save user information: {}", path);
        LocalStorage.loadWrite(path, users.values());
    }
}
