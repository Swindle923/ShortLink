package com.nageoffer.shortlink.project.console.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.project.admin.common.constant.RedisCacheConstant;
import com.nageoffer.shortlink.project.admin.common.enums.UserRoleEnum;
import com.nageoffer.shortlink.project.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.project.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.project.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.project.admin.dao.mapper.UserMapper;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.console.dao.entity.AdminAuditLogDO;
import com.nageoffer.shortlink.project.console.dao.mapper.AdminAuditLogMapper;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleAuditLogPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleLinkPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleLoginReqDTO;
import com.nageoffer.shortlink.project.console.dto.req.ConsoleUserPageReqDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleLinkRespDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleLoginRespDTO;
import com.nageoffer.shortlink.project.console.dto.resp.ConsoleUserRespDTO;
import com.nageoffer.shortlink.project.console.service.ConsoleService;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsoleServiceImpl implements ConsoleService {

    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final ShortLinkMapper shortLinkMapper;
    private final AdminAuditLogMapper auditLogMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public ConsoleLoginRespDTO login(ConsoleLoginReqDTO req) {
        if (StrUtil.isBlank(req.getUsername()) || StrUtil.isBlank(req.getPassword())) {
            throw new ClientException("用户名或密码不能为空");
        }
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, req.getUsername())
                .eq(UserDO::getPassword, req.getPassword())
                .eq(UserDO::getDelFlag, 0);
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            throw new ClientException("用户名或密码错误");
        }
        if (!Objects.equals(userDO.getRole(), UserRoleEnum.ADMIN.name())) {
            throw new ClientException("当前账号不是管理员");
        }
        if (userDO.getStatus() != null && userDO.getStatus() == 1) {
            throw new ClientException("账号已被冻结");
        }
        String key = RedisCacheConstant.USER_LOGIN_KEY + userDO.getUsername();
        Map<Object, Object> exist = stringRedisTemplate.opsForHash().entries(key);
        String token;
        if (!exist.isEmpty()) {
            token = exist.keySet().iterator().next().toString();
            stringRedisTemplate.expire(key, 30L, TimeUnit.MINUTES);
        } else {
            token = UUID.randomUUID().toString();
            stringRedisTemplate.opsForHash().put(key, token, JSON.toJSONString(userDO));
            stringRedisTemplate.expire(key, 30L, TimeUnit.MINUTES);
        }
        return new ConsoleLoginRespDTO(userDO.getUsername(), userDO.getRealName(), userDO.getRole(), token);
    }

    @Override
    public void logout(String username, String token) {
        String key = RedisCacheConstant.USER_LOGIN_KEY + username;
        if (stringRedisTemplate.opsForHash().get(key, token) == null) {
            throw new ClientException("Token 不存在或已失效");
        }
        stringRedisTemplate.delete(key);
    }

    @Override
    public IPage<ConsoleUserRespDTO> pageUsers(ConsoleUserPageReqDTO req) {
        LambdaQueryWrapper<UserDO> wrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getDelFlag, 0)
                .orderByDesc(UserDO::getCreateTime);
        if (StrUtil.isNotBlank(req.getKeyword())) {
            wrapper.and(w -> w.like(UserDO::getUsername, req.getKeyword())
                    .or().like(UserDO::getRealName, req.getKeyword())
                    .or().like(UserDO::getPhone, req.getKeyword()));
        }
        if (StrUtil.isNotBlank(req.getRole())) {
            wrapper.eq(UserDO::getRole, req.getRole());
        }
        if (req.getStatus() != null) {
            wrapper.eq(UserDO::getStatus, req.getStatus());
        }
        Page<UserDO> page = new Page<>(req.getCurrent(), req.getSize());
        IPage<UserDO> result = userMapper.selectPage(page, wrapper);

        List<String> usernames = result.getRecords().stream().map(UserDO::getUsername).collect(Collectors.toList());
        Map<String, Integer> countMap = countLinksByUsername(usernames);

        return result.convert(u -> {
            ConsoleUserRespDTO dto = new ConsoleUserRespDTO();
            BeanUtil.copyProperties(u, dto);
            dto.setLinkCount(countMap.getOrDefault(u.getUsername(), 0));
            return dto;
        });
    }

    private Map<String, Integer> countLinksByUsername(List<String> usernames) {
        Map<String, Integer> result = new HashMap<>();
        if (usernames == null || usernames.isEmpty()) {
            return result;
        }
        LambdaQueryWrapper<GroupDO> q = Wrappers.lambdaQuery(GroupDO.class)
                .in(GroupDO::getUsername, usernames)
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groups = groupMapper.selectList(q);
        if (groups.isEmpty()) {
            usernames.forEach(u -> result.put(u, 0));
            return result;
        }
        Map<String, List<String>> userGidMap = groups.stream()
                .collect(Collectors.groupingBy(GroupDO::getUsername,
                        Collectors.mapping(GroupDO::getGid, Collectors.toList())));
        userGidMap.forEach((user, gids) -> {
            Long count = shortLinkMapper.selectCount(Wrappers.lambdaQuery(ShortLinkDO.class)
                    .in(ShortLinkDO::getGid, gids)
                    .eq(ShortLinkDO::getDelFlag, 0));
            result.put(user, count == null ? 0 : count.intValue());
        });
        usernames.forEach(u -> result.putIfAbsent(u, 0));
        return result;
    }

    @Override
    public void freezeUser(String username) {
        assertTargetUser(username);
        LambdaUpdateWrapper<UserDO> u = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0)
                .set(UserDO::getStatus, 1);
        if (userMapper.update(null, u) < 1) {
            throw new ClientException("用户不存在");
        }
        stringRedisTemplate.delete(RedisCacheConstant.USER_LOGIN_KEY + username);
    }

    @Override
    public void unfreezeUser(String username) {
        LambdaUpdateWrapper<UserDO> u = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0)
                .set(UserDO::getStatus, 0);
        if (userMapper.update(null, u) < 1) {
            throw new ClientException("用户不存在");
        }
    }

    @Override
    public void updateUserRole(String username, String role) {
        if (!UserRoleEnum.isValid(role)) {
            throw new ClientException("角色不合法，仅支持 ADMIN 或 USER");
        }
        assertTargetUser(username);
        LambdaUpdateWrapper<UserDO> u = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, username)
                .eq(UserDO::getDelFlag, 0)
                .set(UserDO::getRole, role);
        if (userMapper.update(null, u) < 1) {
            throw new ClientException("用户不存在");
        }
        stringRedisTemplate.delete(RedisCacheConstant.USER_LOGIN_KEY + username);
    }

    private void assertTargetUser(String username) {
        if (Objects.equals(username, "admin")) {
            throw new ClientException("内置超级管理员不可修改");
        }
    }

    @Override
    public IPage<ConsoleLinkRespDTO> pageLinks(ConsoleLinkPageReqDTO req) {
        LambdaQueryWrapper<ShortLinkDO> wrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getDelFlag, 0)
                .orderByDesc(ShortLinkDO::getCreateTime);
        if (StrUtil.isNotBlank(req.getKeyword())) {
            wrapper.and(w -> w.like(ShortLinkDO::getFullShortUrl, req.getKeyword())
                    .or().like(ShortLinkDO::getOriginUrl, req.getKeyword()));
        }
        if (req.getEnableStatus() != null) {
            wrapper.eq(ShortLinkDO::getEnableStatus, req.getEnableStatus());
        }
        if (StrUtil.isNotBlank(req.getUsername())) {
            LambdaQueryWrapper<GroupDO> gq = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, req.getUsername())
                    .eq(GroupDO::getDelFlag, 0);
            List<String> gids = groupMapper.selectList(gq).stream().map(GroupDO::getGid).toList();
            if (gids.isEmpty()) {
                return new Page<>(req.getCurrent(), req.getSize(), 0);
            }
            wrapper.in(ShortLinkDO::getGid, gids);
        }
        Page<ShortLinkDO> page = new Page<>(req.getCurrent(), req.getSize());
        IPage<ShortLinkDO> result = shortLinkMapper.selectPage(page, wrapper);

        List<String> gids = result.getRecords().stream().map(ShortLinkDO::getGid).distinct().toList();
        Map<String, String> gidToUsername = new HashMap<>();
        if (!gids.isEmpty()) {
            List<GroupDO> groups = groupMapper.selectList(Wrappers.lambdaQuery(GroupDO.class)
                    .in(GroupDO::getGid, gids)
                    .eq(GroupDO::getDelFlag, 0));
            groups.forEach(g -> gidToUsername.put(g.getGid(), g.getUsername()));
        }

        return result.convert(link -> {
            ConsoleLinkRespDTO dto = new ConsoleLinkRespDTO();
            BeanUtil.copyProperties(link, dto);
            dto.setOwnerUsername(gidToUsername.get(link.getGid()));
            return dto;
        });
    }

    @Override
    public void disableLink(String fullShortUrl, String gid) {
        updateLinkEnableStatus(fullShortUrl, gid, 1);
    }

    @Override
    public void enableLink(String fullShortUrl, String gid) {
        updateLinkEnableStatus(fullShortUrl, gid, 0);
    }

    private void updateLinkEnableStatus(String fullShortUrl, String gid, int status) {
        if (StrUtil.isBlank(fullShortUrl) || StrUtil.isBlank(gid)) {
            throw new ClientException("fullShortUrl / gid 不能为空");
        }
        LambdaUpdateWrapper<ShortLinkDO> u = Wrappers.lambdaUpdate(ShortLinkDO.class)
                .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                .eq(ShortLinkDO::getGid, gid)
                .eq(ShortLinkDO::getDelFlag, 0)
                .set(ShortLinkDO::getEnableStatus, status);
        if (shortLinkMapper.update(null, u) < 1) {
            throw new ClientException("短链接不存在");
        }
    }

    @Override
    public IPage<AdminAuditLogDO> pageAuditLogs(ConsoleAuditLogPageReqDTO req) {
        LambdaQueryWrapper<AdminAuditLogDO> wrapper = Wrappers.lambdaQuery(AdminAuditLogDO.class)
                .eq(AdminAuditLogDO::getDelFlag, 0)
                .orderByDesc(AdminAuditLogDO::getCreateTime);
        if (StrUtil.isNotBlank(req.getAdminUsername())) {
            wrapper.eq(AdminAuditLogDO::getAdminUsername, req.getAdminUsername());
        }
        if (StrUtil.isNotBlank(req.getActionType())) {
            wrapper.eq(AdminAuditLogDO::getActionType, req.getActionType());
        }
        if (StrUtil.isNotBlank(req.getTargetType())) {
            wrapper.eq(AdminAuditLogDO::getTargetType, req.getTargetType());
        }
        if (req.getSuccess() != null) {
            wrapper.eq(AdminAuditLogDO::getSuccess, req.getSuccess());
        }
        Page<AdminAuditLogDO> page = new Page<>(req.getCurrent(), req.getSize());
        return auditLogMapper.selectPage(page, wrapper);
    }
}
