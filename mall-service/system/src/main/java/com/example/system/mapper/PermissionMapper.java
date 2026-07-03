package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT * FROM sys_permission WHERE module = #{module} AND status = 0")
    List<Permission> findByModule(@Param("module") String module);

    @Select("SELECT * FROM sys_permission WHERE permission_code = #{code} AND status = 0")
    Permission findByCode(@Param("code") String code);

    /**
     * 通过角色关联查询用户权限码
     */
    @Select("SELECT DISTINCT p.permission_code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.permission_id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 0")
    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户直接授权的权限码
     */
    @Select("SELECT DISTINCT p.permission_code FROM sys_permission p " +
            "INNER JOIN sys_user_permission up ON p.permission_id = up.permission_id " +
            "WHERE up.user_id = #{userId} AND p.status = 0")
    List<String> findDirectPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询角色的权限码
     */
    @Select("SELECT DISTINCT p.permission_code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.permission_id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.status = 0")
    List<String> findPermissionCodesByRoleId(@Param("roleId") Long roleId);
}
