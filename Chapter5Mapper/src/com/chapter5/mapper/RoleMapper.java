package com.chapter5.mapper;

import com.chapter5.entity.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper {
    Role getRole(Long id);
    List<Role> findRolesByMap(Map<String,Object> parameterMap);
}
