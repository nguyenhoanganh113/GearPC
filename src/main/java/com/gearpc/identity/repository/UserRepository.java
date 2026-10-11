package com.gearpc.identity.repository;

import com.gearpc.identity.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {
            "userHasRoles",
            "userHasRoles.role"
    })
    Optional<User> findByEmail(String email);

}
