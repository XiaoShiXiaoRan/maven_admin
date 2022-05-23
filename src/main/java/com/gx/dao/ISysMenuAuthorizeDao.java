package com.gx.dao;

import com.gx.po.SysMenu;
import com.gx.po.SysMenuAuthorize;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("menuAuthorizeDao")
public interface ISysMenuAuthorizeDao {
    /**
     * 根据角色id查询所有的授权
     * @param roleId 角色id
     * @return 授权信息
     */
    List<SysMenu> selectAuthorizeByRoleId(int roleId);

    /**
     * 查询所有的菜单(权限)id 通过 角色id
     * @param roleId 角色id
     * @return 菜单(权限)id集合
     */
    List<Integer> selectMenuIdByRoleId(int roleId);

    /**
     * 新增 授权
     * @param menuAuthorize 授权数据
     * @return 新增结果
     */
    Integer insert(SysMenuAuthorize menuAuthorize);

    /**
     * 修改 授权
     * @param roleId 角色id
     * @param menuId 权限(菜单)id
     * @return 删除结果
     */
    Integer deleteById(@Param("roleId") int roleId,
                       @Param("menuId") int menuId);

    /**
     * 查询角色的授权情况 by 菜单URL
     * @param roleId 角色id
     * @param menuUrl 菜单url
     * @return 是否授权
     */
    Integer hasRoleAuthByMenuUrl(@Param("roleId") int roleId,
                                 @Param("menuUrl") String menuUrl);

    /**
     * 查询角色的授权情况 by 权限标识
     * @param roleId 角色id
     * @param authorize 权限标识
     * @return 是否授权
     */
    Integer hasRoleAuthByAuthorize(@Param("roleId") int roleId,
                                   @Param("authorize") String authorize);

}
