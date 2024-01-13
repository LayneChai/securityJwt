package com.example.demo.config;

import com.example.demo.common.Vo.Result;
import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.service.CustUserService;
import com.example.demo.utils.JwtUtil;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true) // 开启方法安全权限校验
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtFilter; // 注入JwtAuthenticationFilter
    private final JwtUtil jwtUtil; // 注入JwtUtil

    @Autowired
    private CustUserService custUserService;


    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
         return new BCryptPasswordEncoder();
    }

    /**
     * 获取AuthenticationManager 登录验证的时候使用
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.authorizeHttpRequests(
                (authz)->authz
                        .antMatchers("/user/login").permitAll() // 允许匿名用户访问login用于登录
                        .antMatchers("/user/logout").permitAll() // 允许匿名用户访问logout用于登出
                        .antMatchers("/doc.html", "/webjars/**", "/v2/api-docs", "/swagger-resources/**").permitAll() // 允许匿名用户访问swagger
//                        .antMatchers("/test").hasAuthority("test") // 拥有test权限的用户才能访问test接口
                        .anyRequest().authenticated()) // 其他请求必须经过身份验证
                .exceptionHandling(conf->conf // 异常处理
                        .authenticationEntryPoint((req, res, authException) -> { //认证异常

                            log.info("认证异常");
                            res.setContentType( "application/json;charset=utf-8" );
                            res.getWriter().write(new Gson().toJson(Result.error(401, authException.getMessage())));
                        })
                        .accessDeniedHandler((req, res, authException) -> { //权限异常
                            log.info("权限异常");
                            res.setContentType("application/json;charset=utf-8");
                            res.getWriter().write(new Gson().toJson(Result.error(403, authException.getMessage())));
                        })
                )
                .csrf(AbstractHttpConfigurer::disable) // 禁用csrf
                .sessionManagement(conf->conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 禁用session
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)//指定过滤器
                .cors();
        return http.build();
    }
}
