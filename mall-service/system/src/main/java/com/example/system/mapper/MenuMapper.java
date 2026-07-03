package com.example.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.system.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    @Select("SELECT m.* FROM sys_menu m INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id WHERE rm.role_id = #{roleId}")
    List<Menu> findMenusByRoleId(@Param("roleId") Long roleId);
}
