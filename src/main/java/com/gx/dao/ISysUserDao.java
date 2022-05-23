package com.gx.dao;

import com.gx.po.SysUser;
import com.gx.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userDao")
public interface ISysUserDao {

    /**
     * 根据用户名查找用户数据 (login)
     *
     * @param userName 用户名
     * @return 用户数据
     */
    UserVo selectUserByName(String userName);

    /**
     * 查询分页数据
     *
     * @param page          页数
     * @param limit         每页数据条数
     * @param departmentIds 部门id
     * @param userName      用户名
     * @param realName      姓名
     * @param mobile        手机号
     * @param userStatus    用户状态
     * @param startDate     开始时间
     * @param endDate       结束时间
     * @return 分页数据
     */
    List<UserVo> selectPageList(@Param("page") int page,
                                @Param("limit") int limit,
                                @Param("departmentIds") Integer[] departmentIds,
                                @Param("userName") String userName,
                                @Param("realName") String realName,
                                @Param("mobile") String mobile,
                                @Param("userStatus") Integer userStatus,
                                @Param("startDate") String startDate,
                                @Param("endDate") String endDate);

    /**
     * 查询数据 for 导出
     *
     * @param departmentIds 部门id
     * @param userName      用户名
     * @param realName      姓名
     * @param mobile        手机号
     * @param userStatus    用户状态
     * @param startDate     开始时间
     * @param endDate       结束时间
     * @return 分页数据
     */
    List<UserVo> selectForExport(@Param("departmentIds") Integer[] departmentIds,
                                 @Param("userName") String userName,
                                 @Param("realName") String realName,
                                 @Param("mobile") String mobile,
                                 @Param("userStatus") Integer userStatus,
                                 @Param("startDate") String startDate,
                                 @Param("endDate") String endDate);

    /**
     * 根据id查询员工数据
     *
     * @param id 注解
     * @return 员工数据
     */
    SysUser selectById(int id);

    /**
     * 统计员工数据总条数
     *
     * @param departmentIds 部门ids
     * @param userName      用户名
     * @param realName      姓名
     * @param mobile        手机号
     * @param userStatus    用户状态
     * @param startDate     开始时间
     * @param endDate       结束时间
     * @return 职位员工总条数
     */
    int countAll(@Param("departmentIds") Integer[] departmentIds,
                 @Param("userName") String userName,
                 @Param("realName") String realName,
                 @Param("mobile") String mobile,
                 @Param("userStatus") Integer userStatus,
                 @Param("startDate") String startDate,
                 @Param("endDate") String endDate);


    /**
     * 统计在使用 指定职位的用户数
     *
     * @param positionId 职位id
     * @return 用户数
     */
    Integer countUserByPositionId(Integer positionId);
    //
    /**
     * 统计在使用 指定角色的用户数
     *
     * @param roleId 角色id
     * @return 用户数
     */
    Integer countUserByRoleId(int roleId);
    //
    /**
     * 统计在使用 指定部门的用户数
     *
     * @param departmentId 部门id
     * @return 用户数
     */
    Integer countUserByDepartmentId(int departmentId);

    /**
     * 新增用户
     * @param user 用户数据
     * @return 成功否
     */
    Integer insert(SysUser user);

    /**
     * 修改用户
     * @param user 用户数据
     * @return 成功否
     */
    Integer update(SysUser user);

    /**
     * 删除用户
     * @param id 用户id
     * @return 成功否
     */
    Integer deleteById(int id);

    /**
     * 查询返回所有用户
     * @return 所有用户
     */
    List<SysUser> selectAll();
}
