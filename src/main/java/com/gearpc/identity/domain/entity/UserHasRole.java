package com.gearpc.identity.domain.entity;

import com.gearpc.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(
        name = "user_roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_roles_user_role",
                        columnNames = {"user_id", "role_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_user_roles_role_id",
                        columnList = "role_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserHasRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public UserHasRole(User user, Role role) {
        this.user = Objects.requireNonNull(
                user,
                "user must not be null"
        );
        this.role = Objects.requireNonNull(
                role,
                "role must not be null"
        );
    }
}
