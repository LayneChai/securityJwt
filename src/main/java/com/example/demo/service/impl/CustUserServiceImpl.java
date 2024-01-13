package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.domain.CustUser;
import com.example.demo.mapper.SysMenuMapper;
import com.example.demo.service.CustUserService;
import com.example.demo.mapper.CustUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【cust_user(用户表)】的数据库操作Service实现
* @createDate 2024-01-07 14:48:26
*/
@Service
public class CustUserServiceImpl extends ServiceImpl<CustUserMapper, CustUser> implements CustUserService{

    @Autowired
    private CustUserMapper custUserMapper;

    @Autowired
    private SysMenuMapper menuMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<CustUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CustUser::getUsername, username);
        CustUser user = custUserMapper.selectOne(queryWrapper);
        if (user == null) {
            log.error("用户名不存在");
            throw new UsernameNotFoundException("用户名不存在");
        }else {
            List<String> permissions = menuMapper.selectPermsByUserId(user.getId());
            user.setPermissions(permissions); //封装权限
            return user;
        }
    }
}




