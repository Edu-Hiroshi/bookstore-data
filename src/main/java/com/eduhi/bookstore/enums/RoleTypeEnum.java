package com.eduhi.bookstore.enums;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum RoleTypeEnum {
    ROLE_STUDENT,
    ROLE_CUSTOMER,
    ROLE_ADMIN;

    public static RoleTypeEnum findByName(String name) throws NoSuchElementException {
        return Arrays.stream(values())
                .filter(role -> role
                        .name()
                        .equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow();
    }
}
