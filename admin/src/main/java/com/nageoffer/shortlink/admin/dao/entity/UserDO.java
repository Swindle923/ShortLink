package com.nageoffer.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.admin.common.database.BaseDO;
import lombok.Data;

@Data
@TableName("t_user")
public class UserDO extends BaseDO {

    private Long id;

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String mail;

    private String role;

    private Long deletionTime;
}
