package com.gx.dao;

import com.gx.po.SysRole;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色dao接口
 */
@Repository("roleDao")
public interface ISysRoleDao {

    /**
     * 分页查询角色
     * @param page 当前页数
     * @param limit 每页数据条数
     * @param searchName 查询名称
     * @param status 状态
     * @return 分页数据
     */
    List<SysRole> selectForPageList(
            @Param("page") int page,
            @Param("limit") int limit,
            @Param("searchName") String searchName,
            @Param("status") Integer status);

    /**
     * 返回数据总条数
     * @param searchName 查询名称
     * @param status 状态
     * @return 数据总条数
     */
    Integer countAll(@Param("searchName") String searchName,
                 @Param("status") Integer status);


    /**
     * 根据id查询角色信息
     *
     * @return 单条角色信息
     */
    SysRole selectById(int id);

    /**
     * 查询全部角色
     * @return
     */
    List<SysRole> selectAll();

    /**
     * 新增角色
     * @param role 角色数据
     * @return 是否成功
     */
    Integer insert(SysRole role);

    /**
     * 修改角色
     * @param role 角色数据
     * @return 是否成功
     */
    Integer update(SysRole role);

    /**
     * 根际id删除角色信息
     * @param id 主键
     * @return 是否成功
     */
    Integer deleteById(int id);


    /**
     * 更新排序 +1
     * @param minSort 需要更新的最小排序（包含最小） 只是小于传null
     * @param maxValue 需要更新的最大排序（包含最大） 只是大于传null
     * @return 更新结果
     */
    Integer updateSortPlus1(@Param("minSort") Integer minSort,
                            @Param("maxValue") Integer maxValue);

    /**
     * 更新排序 -1
     * @param minSort 需要更新的最小排序（包含最小） 只是小于传null
     * @param maxValue 需要更新的最大排序（包含最大） 只是大于传null
     * @return 更新结果
     */
    Integer updateSortMinus1(@Param("minSort") Integer minSort,
                             @Param("maxValue") Integer maxValue);

}