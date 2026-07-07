package com.houjun.opengauss.domain;


import org.apache.ibatis.annotations.*;



import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String email;
    private Integer age;
}