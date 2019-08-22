package com.chapter8.mapper;

import com.chapter8.entity.PageParams;
import com.chapter8.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper {
    public int insertRole(Role role);
    public int deleteRole(Long id);
    public int updateRole(Role role);
    public Role getRole(Long id);
    public List<Role> findRoles(@Param("pageParam")PageParams pageParams, @Param("roleName") String roleName);
}
