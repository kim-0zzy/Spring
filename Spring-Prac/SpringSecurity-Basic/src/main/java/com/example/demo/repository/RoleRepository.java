package com.example.demo.repository;


import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
// 5-5
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String name);

    @Override
    void delete(Role role);

}