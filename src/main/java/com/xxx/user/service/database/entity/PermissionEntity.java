package com.xxx.user.service.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permissions")
public class PermissionEntity extends BaseEntity {
    @Column(name = "code")
    private String code;
    @Column(name = "value")
    private String value;
}
