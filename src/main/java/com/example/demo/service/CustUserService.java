package com.example.demo.service;

import com.example.demo.domain.CustUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
* @author Administrator
* @description 针对表【cust_user(用户表)】的数据库操作Service
* @createDate 2024-01-07 14:48:26
*/
public interface CustUserService extends IService<CustUser>, UserDetailsService {

}
