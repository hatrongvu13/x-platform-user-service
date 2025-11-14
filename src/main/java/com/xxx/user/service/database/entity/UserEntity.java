package com.xxx.user.service.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accounts")
public class UserEntity extends BaseEntity {
    @Column(unique = true, nullable = false, name = "username")
    private String username;
    @Column(unique = true, nullable = false, name = "full_name")
    private String fullName;
    @Column(unique = true, nullable = false, name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;
}
