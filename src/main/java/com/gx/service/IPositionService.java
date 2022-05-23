package com.gx.service;

import com.gx.po.SysPosition;
import com.gx.vo.JsonMsg;
import com.gx.vo.LayuiTableData;

import java.sql.SQLException;

public interface IPositionService {

    /**
     * 分页查询职位
     * @param page 当前页数
     * @param limit 每页数据条数
     * @param searchName 查询名称
     * @param status 状态
     * @return layuiTable分页数据
     */
    LayuiTableData<SysPosition> selectForPageList(int page,int limit,String searchName,Integer status);

    /**
     * 查询所有数据的条数
     * @return 所有数据的条数
     */
    int countAll();


    /**
     * 根据id查询职位信息
     *
     * @return 单条职位信息
     */
    SysPosition selectById(int id);


    /**
     * 新增职位
     * @param position 职位数据
     * @return 是否成功
     */
    boolean insert(SysPosition position);

    /**
     * 修改职位
     * @param position 职位数据
     * @return 是否成功
     */
    boolean update(SysPosition position);

    /**
     * 根际id删除职位信息
     * @param id 主键
     * @return 是否成功
     */
    JsonMsg deleteById(int id);

}
