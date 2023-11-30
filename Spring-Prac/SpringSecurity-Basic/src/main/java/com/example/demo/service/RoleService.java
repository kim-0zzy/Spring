package com.example.demo.service;

import com.example.demo.entity.Role;

import java.util.List;
// 5-5
public interface RoleService {

    Role getRole(long id);

    List<Role> getRoles();

    void createRole(Role role);

    void deleteRole(long id);
}
