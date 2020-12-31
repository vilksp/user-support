package ksp.vilius.usersupport.enums;

import static ksp.vilius.usersupport.constant.Authority.*;

public enum Role {

    ROLE_USER(USER_AUTHORITIES),

    ROLE_HR(HR_AUTHORITIES),

    ROLE_MANAGER(MANAGER_AUTHORITIES),

    ROLE_ADMIN(ADMIN_AUTHORITIES),

    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String... authority) {
        this.authorities = authority;
    }

    public String[] getAuthorities() {
        return authorities;
    }

}
