package com.houjun.mybatis.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HouJun
 * @date 2021-12-29 21:03
 */
@Data
public class Menu implements Serializable {
    private Integer id;
    private String name;
}
