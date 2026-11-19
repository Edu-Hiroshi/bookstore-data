package com.eduhi.bookstore.repositories;

import com.eduhi.bookstore.models.RolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<RolesEntity, Integer> {

    Optional<RolesEntity> findByRoleName(String roleName);
}
