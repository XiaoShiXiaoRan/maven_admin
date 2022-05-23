package com.gx.dao;

import com.gx.po.SysPosition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 职位dao接口
 */
@Repository("positionDao")
public interface ISysPositionDao {

    /**
     * 分页查询职位
     * @param page 当前页数
     * @param limit 每页数据条数
     * @param searchName 查询名称
     * @param status 状态
     * @return 分页数据
     */
    List<SysPosition> selectForPageList(@Param("page") int page,
                                       @Param("limit")int limit,
                                       @Param("searchName")String searchName,
                                       @Param("status")Integer status);

    /**
     * 返回数据总条数
     * @param searchName 查询名称
     * @param status 状态
     * @return 数据总条数
     */
    Integer countAll(@Param("searchName")String searchName,
                 @Param("status") Integer status);


    /**
     * 根据id查询职位信息
     *
     * @return 单条职位信息
     */
    SysPosition selectById(int id);

    /**
     * 查询全部职位
     * @return
     */
    List<SysPosition> selectAll();

    /**
     * 新增职位
     * @param position 职位数据
     * @return 是否成功
     */
    Integer insert(SysPosition position);

    /**
     * 修改职位
     * @param position 职位数据
     * @return 是否成功
     */
    Integer update(SysPosition position);

    /**
     * 根据id删除职位信息
     * @param id 主键
     * @return 是否成功
     */
    Integer deleteById(Integer id);


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
