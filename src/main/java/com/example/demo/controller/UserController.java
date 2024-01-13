package com.example.demo.controller;


import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.Vo.AuthUserVo;
import com.example.demo.common.Vo.Result;
import com.example.demo.domain.CustUser;
import com.example.demo.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static com.example.demo.common.constants.OtherConstants.AUTH_TOKEN;
import static com.example.demo.common.constants.OtherConstants.USER_PREFIX;


@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AuthenticationManager authenticateManager;

    @Autowired
    private JwtUtil jwtUtil;

    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody CustUser user) {
        Result result = new Result();
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authenticate = authenticateManager.authenticate(usernamePasswordAuthenticationToken);
            if (authenticate == null) {
                return Result.error(401, "登录校验失败");
            } else {
                user = (CustUser) authenticate.getPrincipal();
                String token = jwtUtil.createJwt(user);//创建token
                AuthUserVo authUserVo = new AuthUserVo();
                BeanUtils.copyProperties(user, authUserVo);
                authUserVo.setToken(token);
    //            将token放在redis中
                redisTemplate.opsForValue().set(USER_PREFIX + String.valueOf(user.getId()),user,30, TimeUnit.MINUTES);
                return Result.success(authUserVo);
            }
        } catch (Exception e) {
            return result.error500("登陆失败");
        }
    }


    @ApiOperation(value = "用户退出")
    @GetMapping("/logout")
    public Result logout(HttpServletRequest req) {
        String token = req.getHeader(AUTH_TOKEN);
        if (token == null || "".equals(token)) {
            return Result.error(401, "token为空");
        } else {
            DecodedJWT decodedJWT = jwtUtil.resolveJwt(token);
            CustUser user = jwtUtil.toUser(decodedJWT);
            redisTemplate.delete(USER_PREFIX + String.valueOf(user.getId()));
            return Result.success("退出成功");
        }
    }
}
