package com.example.demo.Vo;


import com.example.demo.domain.CustUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserVo extends CustUser {
    private String token;
}
