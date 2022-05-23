package com.gx.dao;

import com.gx.po.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("menuDao")
public interface ISysMenuDao {

    /**
     * 根据角色ID查菜单
     * @param roleId 角色id
     * @return 菜单list
     */
    List<SysMenu> selectMenuByRoleId(Integer roleId);
    /**
     * 查询所有菜单信息
     * @return list
     */
    List<SysMenu> selectAll();

    /**
     * 查询菜单数据 by 主键
     * @param id 主键
     * @return 菜单数据
     */
    SysMenu selectById(int id);

    /**
     * 根据父id查询菜单个数
     * @param pid 父id
     * @return 菜单个数
     */
    Integer countAllByPid(int pid);

    /**
     * 新增菜单
     * @param menu 菜单数据
     * @return 是否成功
     */
    Integer insert(SysMenu menu);

    /**
     * 修改菜单
     * @param menu 菜单数据
     * @return 是否成功
     */
    Integer update(SysMenu menu);

    /**
     * 删除菜单
     * @param id 菜单id
     * @return 是否成功
     */
    Integer deleteById(int id);

    /**
     * 更新排序 +1 by pid
     * @param pid 父id
     * @param minSort 需要更新的最小排序（包含最小） 只是小于传null
     * @param maxValue 需要更新的最大排序（包含最大） 只是大于传null
     * @return 更新结果
     */
    boolean updateSortPlus1(@Param("pid") int pid,
                            @Param("minSort") Integer minSort,
                            @Param("maxValue") Integer maxValue);

    /**
     * 更新排序 -1 by pid
     * @param pid 父id
     * @param minSort 需要更新的最小排序（包含最小） 只是小于传null
     * @param maxValue 需要更新的最大排序（包含最大） 只是大于传null
     * @return 更新结果
     */
    boolean updateSortMinus1(@Param("pid") int pid,
                             @Param("minSort") Integer minSort,
                             @Param("maxValue") Integer maxValue);



}
