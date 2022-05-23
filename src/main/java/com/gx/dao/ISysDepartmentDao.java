package com.gx.dao;

import com.gx.po.SysDepartment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("departmentDao")
public interface ISysDepartmentDao {

    /**
     * 查询所有部门信息
     * @return list
     */
    List<SysDepartment> selectAll();

    /**
     * 查询部门数据 by 主键
     * @param id 主键
     * @return 部门数据
     */
    SysDepartment selectById(int id);

    /**
     * 根据父id查询部门个数
     * @param pid 父id
     * @return 部门个数
     */
    Integer countAllByPid(int pid);

    /**
     * 新增部门
     * @param department 部门数据
     * @return 是否成功
     */
    Integer insert(SysDepartment department);

    /**
     * 修改部门
     * @param department 部门数据
     * @return 是否成功
     */
    Integer update(SysDepartment department);

    /**
     * 删除部门
     * @param id 部门id
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
    Integer updateSortPlus1(@Param("pid") int pid,
                            @Param("minSort") Integer minSort,
                            @Param("maxValue") Integer maxValue);

    /**
     * 更新排序 -1 by pid
     * @param pid 父id
     * @param minSort 需要更新的最小排序（包含最小） 只是小于传null
     * @param maxValue 需要更新的最大排序（包含最大） 只是大于传null
     * @return 更新结果
     */
    Integer updateSortMinus1(@Param("pid") int pid,
                             @Param("minSort") Integer minSort,
                             @Param("maxValue") Integer maxValue);
}
