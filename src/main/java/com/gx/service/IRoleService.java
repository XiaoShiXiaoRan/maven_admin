package com.gx.service;

import com.gx.po.SysRole;
import com.gx.vo.JsonMsg;
import com.gx.vo.LayuiTableData;
import com.gx.vo.LayuiTreeVo;

import java.util.List;

public interface IRoleService {

    /**
     * 分页查询角色
     * @param page 当前页数
     * @param limit 每页数据条数
     * @param searchName 查询名称
     * @param status 状态
     * @return layuiTable分页数据
     */
    LayuiTableData<SysRole> selectForPageList(int page, int limit, String searchName, Integer status);

    /**
     * 查询所有数据的条数
     * @return 所有数据的条数
     */
    int countAll();


    /**
     * 根据id查询角色信息
     *
     * @return 单条角色信息
     */
    SysRole selectById(int id);


    /**
     * 新增角色
     * @param role 角色数据
     * @return 是否成功
     */
    boolean insert(SysRole role);

    /**
     * 修改角色
     * @param role 角色数据
     * @return 是否成功
     */
    boolean update(SysRole role);

    /**
     * 根际id删除角色信息
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(int id);

    //授权相关代码

    /**
     * 查询菜单for layui tree
     * @param roleId 角色
     * @return 数据
     */
    List<LayuiTreeVo> selectMenuForLayuiTree(int roleId);


    /**
     * 角色授权操作
     * @param roleId 授权的角色id
     * @param selectMenuIdList 勾选的角色权限id的集合
     * @return 授权结果
     */
    JsonMsg authorize(int roleId, List<Integer> selectMenuIdList);

}
