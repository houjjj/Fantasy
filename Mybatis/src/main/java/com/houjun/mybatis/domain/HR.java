package com.houjun.mybatis.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HouJun
 * @date 2021-12-25 15:37
 */
@Data
public class HR implements Serializable{

    private Integer id;
    private String name;
    private String phone;
}
