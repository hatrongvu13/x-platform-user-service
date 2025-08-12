package com.xxx.user.service.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "other_info")
public class OtherInfoEntity extends BaseEntity {
    @Column(name = "identity_number")
    private String identityNumber;
    @Column(name = "real_name")
    private String realName;
    @Column(name = "address")
    private String address;
}
