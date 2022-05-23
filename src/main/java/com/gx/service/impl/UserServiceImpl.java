package com.gx.service.impl;

import com.gx.dao.ISysDepartmentDao;
import com.gx.dao.ISysPositionDao;
import com.gx.dao.ISysRoleDao;
import com.gx.dao.ISysUserDao;
import com.gx.exception.MyDataException;
import com.gx.po.SysDepartment;
import com.gx.po.SysPosition;
import com.gx.po.SysRole;
import com.gx.po.SysUser;
import com.gx.service.IUserService;
import com.gx.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {
    //dao
    @Autowired
    private ISysDepartmentDao departmentDao;
    @Autowired
    private ISysPositionDao positionDao;
    @Autowired
    private ISysRoleDao roleDao;
    @Autowired
    private ISysUserDao userDao;
    @Autowired

    @Override
    public List<LayuiTreeVo> selectDepartmentForTree() {
        List<SysDepartment> departmentList = this.departmentDao.selectAll();
        return dealLayuiTreeVo(departmentList,0);
    }

    @Override
    public LayuiTableData<UserVo> selectPageList(int page, int limit, Integer departmentId, String userName, String realName, String mobile, Integer userStatus, String startDate, String endDate) {
        //获取所选部门的子节点的id
        Integer[] departmentIds=new Integer[]{};
        if (departmentId!=null){
            List<SysDepartment> departmentList = this.departmentDao.selectAll();
            //查找选中部门id 下的子节点id
            List<Integer> departmentIdList=getChildrenIds(departmentList,departmentId);
            //
            departmentIdList.add(departmentId);
            //List 转 数组
            departmentIds=new Integer[departmentIdList.size()];
            departmentIdList.toArray(departmentIds);
        }

        List<UserVo> data=this.userDao.selectPageList(page,limit,departmentIds,userName,realName,mobile,userStatus,startDate,endDate);
        int count=this.userDao.countAll(departmentIds,userName,realName,mobile,userStatus,startDate,endDate);

        return new LayuiTableData<>(count,data);
    }

    @Override
    public List<UserVo> selectForExport(Integer departmentId, String userName, String realName, String mobile, Integer userStatus, String startDate, String endDate) {
        //获取所选部门的子节点的id
        Integer[] departmentIds=new Integer[]{};
        if (departmentId!=null){
            List<SysDepartment> departmentList = this.departmentDao.selectAll();
            //查找选中部门id 下的子节点id
            List<Integer> departmentIdList=getChildrenIds(departmentList,departmentId);
            //
            departmentIdList.add(departmentId);
            //List 转 数组
            departmentIds=new Integer[departmentIdList.size()];
            departmentIdList.toArray(departmentIds);
        }

        return this.userDao.selectForExport(departmentIds,userName,realName,mobile,userStatus,startDate,endDate);
    }

    @Override
    public SysUser selectById(int id) {
        return this.userDao.selectById(id);
    }

    @Override
    public List<TreeSelectVo> selectForTreeSelect() {
        //获取所有的部门数据
        List<SysDepartment> departmentList = this.departmentDao.selectAll();
        return this.dealTreeSelect(departmentList,0);
    }

    @Override
    public List<H5SelectVo> selectPositionForH5Select() {
        List<SysPosition> positionList=this.positionDao.selectAll();
        List<H5SelectVo> rList=new ArrayList<>();
        for (SysPosition position:positionList) {
            rList.add(new H5SelectVo(String.valueOf(position.getId()),position.getPositionName()));
        }
        return rList;
    }

    @Override
    public List<H5SelectVo> selectRoleForH5Select() {
        List<SysRole> roleList=this.roleDao.selectAll();
        List<H5SelectVo> rList=new ArrayList<>();
        for (SysRole role:roleList) {
            rList.add(new H5SelectVo(String.valueOf(role.getId()),role.getRoleName()));
        }
        return rList;
    }


    @Override
    public boolean insert(SysUser user) {
        return this.userDao.insert(user)>0;
    }

    @Override
    public boolean update(SysUser user) {
        return this.userDao.update(user)>0;
    }

    @Override
    public boolean deleteById(int id) {
        return this.userDao.deleteById(id)>0;
    }

    @Override
    public List<SysUser> selectAll() {
        return this.userDao.selectAll();
    }

    @Override
    public boolean saveUpload(List<SysUser> saveSysUsers) {
        //循环调用dao层的新增
        for (SysUser saveUser : saveSysUsers) {
            if (this.userDao.insert(saveUser) < 1) {
                //失败
                throw new MyDataException(saveUser.getUserName() + "新增失败");
            }
        }
        //全部成功
        return true;
    }


    /**
     * List<SysDepartment> 转 List<LayuiTreeVo>
     *
     * @param listSource 部门数据
     * @param pid        父节点
     * @return List<LayuiTreeVo>数据
     */
    private List<LayuiTreeVo> dealLayuiTreeVo(List<SysDepartment> listSource, int pid) {
        List<LayuiTreeVo> rList = new ArrayList<>();
        LayuiTreeVo layuiTreeVo=null;
        for (SysDepartment department : listSource) {
            if (department.getParentId()==pid){
                layuiTreeVo=new LayuiTreeVo();
                layuiTreeVo.setId(department.getId());
                layuiTreeVo.setTitle(department.getDepartmentName());
                layuiTreeVo.setSpread(true);//展开
                //获取子节点
                List<LayuiTreeVo> listChildren=dealLayuiTreeVo(listSource,department.getId());
                if (listChildren.size()>0){
                    layuiTreeVo.setChildren(listChildren);
                }
                rList.add(layuiTreeVo);
            }
        }
        return rList;
    }

    private List<Integer> getChildrenIds(List<SysDepartment> listSource, int pid) {
        List<Integer> rList = new ArrayList<>();
        for (SysDepartment department : listSource) {
            if (department.getParentId()==pid){
                rList.add(department.getId());
                //把子节点list添加到 父节点List
                rList.addAll(getChildrenIds(listSource,department.getId()));
            }
        }
        return rList;
    }





    private List<TreeSelectVo> dealTreeSelect(List<SysDepartment> listSource, int pid) {
        List<TreeSelectVo> rList = new ArrayList<>();
        TreeSelectVo treeSelectVo=null;
        for (SysDepartment department : listSource) {
            if (department.getParentId()==pid){
                treeSelectVo=new TreeSelectVo();
                treeSelectVo.setId(department.getId());//id
                treeSelectVo.setName(department.getDepartmentName());//显示的值
                treeSelectVo.setChecked(false);
                treeSelectVo.setOpen(true);//默认展开

                List<TreeSelectVo> children=this.dealTreeSelect(listSource,department.getId());
                if (children.size()>0){
                    //有子节点
                    treeSelectVo.setChildren(children);
                }else {
                    //没有子节点
                    treeSelectVo.setChildren(null);
                }
                rList.add(treeSelectVo);
            }
        }

        return rList;
    }
}
