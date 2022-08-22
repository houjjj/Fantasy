package com.hou.stream.optional;

import java.util.Optional;

/**
 * @author HouJun
 * @date 2021-12-25 19:20
 */
public class OptionalTest {
    public static void main(String[] args) {
        Boolean success = true;
        Optional<Boolean> optional = Optional.ofNullable(success);
        System.out.println(optional.isPresent());
        optional.ifPresent(System.out::println);
    }
}
