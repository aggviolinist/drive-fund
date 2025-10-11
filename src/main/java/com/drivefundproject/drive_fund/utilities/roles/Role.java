package com.drivefundproject.drive_fund.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    USER(Collections.emptySet()),
    ADMIN(
        Set.of(
            Permission.ADMIN_READ,
            Permission.ADMIN_CREATE,
            Permission.ADMIN_UPDATE,
            Permission.ADMIN_DELETE
        )
    ), 
    MANAGER(
        Set.of(
            Permission.MANAGER_READ,
            Permission.MANAGER_CREATE,
            Permission.MANAGER_UPDATE
        )
    );
    @Getter
    private final Set<Permission> Permissionz;  

    public List<SimpleGrantedAuthority> getAuthorities(){
        List<SimpleGrantedAuthority> authorities = getPermissionz()
               .stream()
               .map(perm -> new SimpleGrantedAuthority(perm.name()))
               .collect(Collectors.toList());
            authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
            return authorities;

    }
}
