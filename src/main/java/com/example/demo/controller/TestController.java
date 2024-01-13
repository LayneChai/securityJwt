package com.example.demo.controller;


import com.example.demo.domain.CustUser;
import com.example.demo.service.CustUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试类")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private CustUserService custUserService;

    @ApiOperation(value = "测试方法")
    @PreAuthorize("hasAuthority('test')")  // 权限验证 需要用户具有test权限
    @GetMapping("/test")
    public CustUser test(){
        return custUserService.getById(1);
    }

}


