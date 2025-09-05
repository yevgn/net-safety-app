package ru.obninsk.net_safety_app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER (
            List.of(
                    Permission.CREATE,
                    Permission.READ,
                    Permission.UPDATE,
                    Permission.DELETE
            )
    )

    ;

    private final List<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
