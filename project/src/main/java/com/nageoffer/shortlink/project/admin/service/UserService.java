package com.nageoffer.shortlink.project.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.project.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.project.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.project.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.project.admin.dto.req.UserRoleUpdateReqDTO;
import com.nageoffer.shortlink.project.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.project.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.project.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {

    UserRespDTO getUserByUsername(String username);

    Boolean hasUsername(String username);

    void register(UserRegisterReqDTO requestParam);

    void update(UserUpdateReqDTO requestParam);

    void updateRole(UserRoleUpdateReqDTO requestParam);

    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    Boolean checkLogin(String username, String token);

    void logout(String username, String token);
}
