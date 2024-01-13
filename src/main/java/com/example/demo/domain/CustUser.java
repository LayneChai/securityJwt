package com.example.demo.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户表
 * @TableName cust_user
 */
@TableName(value ="cust_user")
@Data
public class CustUser implements Serializable, UserDetails {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String nickname;

    private Integer enable;

    private String password;

    @TableField(exist = false)
    private List<String> permissions;  //权限集合

    //存储SpringSecurity所需要的权限信息的集合
    //安全限制，不允许序列化
    @JSONField(serialize = false)
    @JsonIgnore
    @TableField(exist = false)
    private List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        if (this.authorities == null) {
            //把permissions中字符串类型的权限信息转换成GrantedAuthority对象存入authorities中
            this.authorities = this.permissions.stream().distinct().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}