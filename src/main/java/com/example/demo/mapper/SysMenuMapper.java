package com.example.demo.mapper;

import com.example.demo.domain.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【sys_menu(菜单表)】的数据库操作Mapper
* @createDate 2024-01-07 14:48:26
* @Entity com.example.demo.domain.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenu> {
    List<String> selectPermsByUserId(Integer id);
}


