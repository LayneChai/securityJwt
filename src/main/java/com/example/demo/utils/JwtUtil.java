package com.example.demo.utils;

/**
 * @author: zsqt
 * Package:  com.zsqt.security.utils
 * @date: 2024/1/3 16:52
 * @Description:
 * @version: 1.0
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.domain.CustUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.example.demo.common.constants.OtherConstants.*;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private static final String key = "ims_test"; // 密钥
    private final RedisTemplate redisTemplate; //

    /**
     * 创建jwt
     *
     * @param details 用户登陆信息
     * @return String
     */
    public String createJwt(CustUser details) {
        Algorithm algorithm = Algorithm.HMAC256(key);
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("user",details.getId()) //把用户id存入token中
                .withExpiresAt(expireTime())
                .withIssuedAt(new Date())// 签发时间
                .sign(algorithm);
    }

    /**
     * 解析token
     * @param authToken token
     * @return DecodedJWT
     */
    public DecodedJWT resolveJwt(String authToken) {
        String token = convertToken(authToken);
        if (token == null)
            return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = jwtVerifier.verify(token); // 验证token
            Date expiresAt = verify.getExpiresAt(); // token过期时间
            return new Date().after(expiresAt) ? null : verify; // 判断是否过期
        } catch (JWTVerificationException jwtVerificationException) {
            return null;
        }
    }

    /**
     * 检查用户在redis中是否存在
     * @param user
     * @return
     */
    public boolean checkRedis(CustUser user) {
        if(redisTemplate.opsForValue().get(USER_PREFIX + user.getId()) == null){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 根据解析后的token获取用户信息
     *
     */
    public CustUser toUser(DecodedJWT jwt) {
        Integer uid;
        try {
            Map<String, Claim> claims = jwt.getClaims();
            uid = claims.get("user").asInt();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        CustUser user = (CustUser) redisTemplate.opsForValue().get(USER_PREFIX +uid);
        if(Objects.isNull(user)){
            throw new RuntimeException("用户未登录");
        }
        return user;
    }

    /**
     * 在请求头信息中获取token 去掉对应的前缀
     */
    private String convertToken(String authToken) {
        if (!StringUtils.hasText(authToken) || !authToken.startsWith("Bearer "))
            return null;
        return authToken.substring(TOKEN_SITE);// 截取token 因为token前面有Bearer空格
    }

    /**
     * 设置token过期时间
     * @return
     */
    private Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        return calendar.getTime();
    }
}