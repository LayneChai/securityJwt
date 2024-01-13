package com.example.demo.filter;

import com.auth0.jwt.interfaces.DecodedJWT;

import com.example.demo.domain.CustUser;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.example.demo.common.constants.OtherConstants.AUTH_TOKEN;


/**
 * @author: YinLei
 * Package:  com.zsqt.security.filter
 * @date: 2024/1/3 16:40
 * @Description:
 * @version: 1.0
 */

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(AUTH_TOKEN);

        if (!StringUtils.hasText(token)) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }

        DecodedJWT jwt = jwtUtil.resolveJwt(token); //解析token
        if (jwt != null) {//如果token有效，将用户信息放入到SecurityContext中
            CustUser user = jwtUtil.toUser(jwt);
            if (Objects.isNull(user)) {
                throw new RuntimeException("用户未登录");
            }
            if (jwtUtil.checkRedis(user)) {// 验证redis中是否存在该用户
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); //这个方法将用户信息放入到SecurityContext中 手动设置用户信息
            }else {
                throw new RuntimeException("Token失效");
            }
        }else{
            throw new RuntimeException("Token失效");
        }
        filterChain.doFilter(request, response); // 如果token无效，进行下一个过滤器
    }
}
