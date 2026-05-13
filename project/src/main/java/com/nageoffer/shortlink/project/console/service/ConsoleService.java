package com.nageoffer.shortlink.project.console.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nageoffer.shortlink.project.console.dao.entity.AdminAuditLogDO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleAuditLogPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleLinkPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleLoginReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleUserPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleLinkRespDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleLoginRespDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleUserRespDTO;

public interface ConsoleService {

    ConsoleLoginRespDTO login(ConsoleLoginReqDTO req);

    void logout(String username, String token);

    IPage<ConsoleUserRespDTO> pageUsers(ConsoleUserPageReqDTO req);

    void freezeUser(String username);

    void unfreezeUser(String username);

    void updateUserRole(String username, String role);

    IPage<ConsoleLinkRespDTO> pageLinks(ConsoleLinkPageReqDTO req);

    void disableLink(String fullShortUrl, String gid);

    void enableLink(String fullShortUrl, String gid);

    IPage<AdminAuditLogDO> pageAuditLogs(ConsoleAuditLogPageReqDTO req);
}
