package com.gx.service.impl;

import com.gx.dao.ISysDepartmentDao;
import com.gx.dao.ISysUserDao;
import com.gx.exception.MyDataException;
import com.gx.po.SysDepartment;
import com.gx.service.IDepartmentService;
import com.gx.vo.DepartmentTableTreeVo;
import com.gx.vo.LayuiTableData;
import com.gx.vo.TreeSelectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("departmentService")
@Transactional
public class DepartmentServiceImpl implements IDepartmentService {
    //dao层
    @Autowired
    private ISysDepartmentDao departmentDao;
    @Autowired
    private ISysUserDao userDao;

    @Override
    public LayuiTableData<DepartmentTableTreeVo> selectForTable() {
        List<SysDepartment> departmentList = this.departmentDao.selectAll();
        List<DepartmentTableTreeVo> data = dealDepartmentTableTreeList(departmentList, 0);

        //组装layui table数据
        LayuiTableData<DepartmentTableTreeVo> layuiTableData = new LayuiTableData<>();
        layuiTableData.setCount(data.size());
        layuiTableData.setData(data);
        return layuiTableData;
    }

    @Override
    public List<TreeSelectVo> SelectForTreeSelect() {
        //获取所有的部门数据
        List<SysDepartment> departmentList = this.departmentDao.selectAll();
        List<TreeSelectVo> rList = new ArrayList<>();
        //根 不存在，添加是为了方便选择
        TreeSelectVo root = new TreeSelectVo();
        root.setId(0);
        root.setName("根");
        root.setChecked(false);
        root.setOpen(true);
        root.setChildren(this.dealTreeSelect(departmentList, 0));
        rList.add(root);
        return rList;
    }

    @Override
    public SysDepartment selectById(int id) {
        return this.departmentDao.selectById(id);
    }

    @Override
    public int countAllByPid(int pid) {
        return this.departmentDao.countAllByPid(pid);
    }

    @Override
    public boolean insert(SysDepartment department) {
        int nextSort = this.countAllByPid(department.getParentId()) + 1;
        //判断是否是 小于nextSort，是,调小的序号 需要处理其他序号
        if (department.getDepartmentSort() < nextSort) {
            this.departmentDao.updateSortPlus1(department.getParentId(), department.getDepartmentSort(), null);
        }
        //新增
        boolean isOk = this.departmentDao.insert(department) > 0;
        if (!isOk) {
            throw new MyDataException("新增操作失败：" + department);
        }
        return true;
    }

    @Override
    public boolean update(SysDepartment department) {
        //查询出未修改的数据
        SysDepartment dbDepartment = this.departmentDao.selectById(department.getId());
        //判断上级部门是否改变
        if (!dbDepartment.getParentId().equals(department.getParentId())) {
            //上级部门 发生改变
            //对于旧上级部门 大于原来的sort -1
            this.departmentDao.updateSortMinus1(dbDepartment.getParentId(), (dbDepartment.getDepartmentSort() + 1), null);

            //对于新的上级部门 大于等于新sort =1
            this.departmentDao.updateSortPlus1(department.getParentId(), department.getDepartmentSort(), null);

        } else {
            //上级部门没有发生改变

            //判断修改后的序号 和 修改前的序号
            if (department.getDepartmentSort() < dbDepartment.getDepartmentSort()) {
                //向前（小）移  大于等于新sort，小于旧的sort +1
                this.departmentDao.updateSortPlus1(dbDepartment.getParentId(), department.getDepartmentSort(), (dbDepartment.getDepartmentSort() - 1));

            } else {
                //向后（大）移  大于旧sort，小于等于新的sort -1
                this.departmentDao.updateSortMinus1(dbDepartment.getParentId(), (dbDepartment.getDepartmentSort() + 1), department.getDepartmentSort());

            }
        }

        //修改
        boolean isOk = this.departmentDao.update(department) > 0;
        if (!isOk) {
            throw new RuntimeException("修改操作失败：update");
        }
        return true;
    }

    @Override
    public boolean deleteById(int id) {
        //判判断是否有使用的用户
        int countUsed = this.userDao.countUserByDepartmentId(id);
        if (countUsed == 0) {
            //判断是否有子节点
            int countChildren = this.countAllByPid(id);
            if (countChildren == 0) {
                //查询出未修改的数据
                SysDepartment dbDepartment = this.departmentDao.selectById(id);
                //处理序号  大于sort -1
                this.departmentDao.updateSortMinus1(dbDepartment.getParentId(), dbDepartment.getDepartmentSort() + 1, null);

                //修改
                boolean isOk = this.departmentDao.deleteById(id) > 0;
                if (!isOk) {
                    throw new MyDataException("删除操作失败：deleteById");
                }
            } else {
                throw new MyDataException("该部门存在子部门，不能直接删除");
            }
        } else {
            throw new MyDataException("该部门在使用中，不能删除");
        }

        return true;
    }


    /**
     * 处理 tableTree数据
     * List<SysDepartment> 转换 List<DepartmentTableTreeVo>
     *
     * @param listSource 源数据
     * @param pid        父id
     * @return 处理后的数据
     */
    private List<DepartmentTableTreeVo> dealDepartmentTableTreeList(List<SysDepartment> listSource, int pid) {
        List<DepartmentTableTreeVo> rList = new ArrayList<>();
        DepartmentTableTreeVo departmentTableTreeVo;
        for (SysDepartment department : listSource) {
            if (department.getParentId() == pid) {
                departmentTableTreeVo = new DepartmentTableTreeVo();

                departmentTableTreeVo.setId(department.getId());
                departmentTableTreeVo.setGmtCreate(department.getGmtCreate());
                departmentTableTreeVo.setParentId(department.getParentId());
                departmentTableTreeVo.setDepartmentName(department.getDepartmentName());
                departmentTableTreeVo.setTelephone(department.getTelephone());
                departmentTableTreeVo.setFax(department.getFax());
                departmentTableTreeVo.setEmail(department.getEmail());
                departmentTableTreeVo.setPrincipal(department.getPrincipal());
                departmentTableTreeVo.setDepartmentSort(department.getDepartmentSort());
                departmentTableTreeVo.setRemark(department.getRemark());
                //子节点
                departmentTableTreeVo.setTreeList(dealDepartmentTableTreeList(listSource, department.getId()));
                rList.add(departmentTableTreeVo);
            }
        }
        return rList;
    }


    private List<TreeSelectVo> dealTreeSelect(List<SysDepartment> listSource, int pid) {
        List<TreeSelectVo> rList = new ArrayList<>();
        TreeSelectVo treeSelectVo;
        for (SysDepartment department : listSource) {
            if (department.getParentId() == pid) {
                treeSelectVo = new TreeSelectVo();
                treeSelectVo.setId(department.getId());//id
                treeSelectVo.setName(department.getDepartmentName());//显示的值
                treeSelectVo.setChecked(false);
                treeSelectVo.setOpen(true);//默认展开

                List<TreeSelectVo> children = this.dealTreeSelect(listSource, department.getId());
                if (children.size() > 0) {
                    //有子节点
                    treeSelectVo.setChildren(children);
                } else {
                    //没有子节点
                    treeSelectVo.setChildren(null);
                }
                rList.add(treeSelectVo);
            }
        }

        return rList;
    }

}
