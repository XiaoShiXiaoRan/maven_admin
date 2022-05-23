package com.gx.service.impl;

import com.gx.dao.ISysMenuDao;
import com.gx.exception.MyDataException;
import com.gx.po.SysMenu;
import com.gx.service.IMenuService;
import com.gx.vo.LayuiTableData;
import com.gx.vo.MenuTableTreeVo;
import com.gx.vo.TreeSelectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service("menuService")
@Transactional
public class MenuServiceImpl implements IMenuService {
    //dao
    @Autowired
    private ISysMenuDao menuDao;
    @Override
    public List<MenuTableTreeVo> selectMenuByRoleId(Integer roleId) {
        //调用dao查询角色对应的菜单
        List<SysMenu> menuList = this.menuDao.selectMenuByRoleId(roleId);
        return dealMenuTableTreeVoList(menuList, 0);
    }

    @Override
    public LayuiTableData<MenuTableTreeVo> selectForTable() {
        List<SysMenu> menuList = this.menuDao.selectAll();
        List<MenuTableTreeVo> data = dealMenuTableTreeVoList(menuList, 0);

        //组装layui table数据
        LayuiTableData<MenuTableTreeVo> layuiTableData = new LayuiTableData<>();
        layuiTableData.setCount(data.size());
        layuiTableData.setData(data);
        return layuiTableData;
    }

    @Override
    public List<TreeSelectVo> SelectForTreeSelect() {
        //获取所有的部门数据
        List<SysMenu> departmentList = this.menuDao.selectAll();
        List<TreeSelectVo> rList=new ArrayList<>();
        //根 不存在，添加是为了方便选择
        TreeSelectVo root=new TreeSelectVo();
        root.setId(0);
        root.setName("根");
        root.setChecked(false);
        root.setOpen(true);
        root.setChildren(this.dealTreeSelect(departmentList,0));
        rList.add(root);
        return rList;
    }

    @Override
    public SysMenu selectById(int id) {
        return this.menuDao.selectById(id);
    }

    @Override
    public int countAllByPid(int pid) {
        return this.menuDao.countAllByPid(pid);
    }

    @Override
    public boolean insert(SysMenu menu) {
        int nextSort = this.countAllByPid(menu.getParentId()) + 1;
        //判断是否是 小于nextSort，是,调小的序号 需要处理其他序号
        if (menu.getMenuSort() < nextSort) {
            this.menuDao.updateSortPlus1(menu.getParentId(), menu.getMenuSort(), null);
        }
        //新增
        boolean isOk = this.menuDao.insert(menu)>0;
        if (!isOk) {
            throw new MyDataException("新增操作失败：" + menu);
        }
        return true;
    }

    @Override
    public boolean update(SysMenu menu) {
        //查询出未修改的数据
        SysMenu dbMenu=this.menuDao.selectById(menu.getId());
        //判断上级部门是否改变
        if (!dbMenu.getParentId().equals(menu.getParentId())){
            //上级部门 发生改变
            //对于旧上级部门 大于原来的sort -1
            this.menuDao.updateSortMinus1(dbMenu.getParentId(), (dbMenu.getMenuSort()+1), null);
            //对于新的上级部门 大于等于新sort =1
            this.menuDao.updateSortPlus1(menu.getParentId(), menu.getMenuSort(), null);
        }else {
            //上级部门没有发生改变
            //判断修改后的序号 和 修改前的序号
            if (menu.getMenuSort()<dbMenu.getMenuSort()){
                //向前（小）移  大于等于新sort，小于旧的sort +1
                this.menuDao.updateSortPlus1(dbMenu.getParentId(), menu.getMenuSort(), (dbMenu.getMenuSort()-1));
            }else {
                //向后（大）移  大于旧sort，小于等于新的sort -1
                this.menuDao.updateSortMinus1(dbMenu.getParentId(), (dbMenu.getMenuSort()+1), menu.getMenuSort());
            }
        }

        //修改
        boolean isOk=this.menuDao.update(menu)>0;
        if (!isOk){
            throw new MyDataException("修改操作失败：update");
        }
        return true;
    }

    @Override
    public boolean deleteById(int id) {
        //判断是否有子节点
        int countChildren=this.countAllByPid(id);
        if (countChildren==0){
            //查询出未修改的数据
            SysMenu dbMenu=this.menuDao.selectById(id);
            //处理序号  大于sort -1
            this.menuDao.updateSortMinus1(dbMenu.getParentId(), dbMenu.getMenuSort()+1,null);

            //修改
            boolean isOk=this.menuDao.deleteById(id)>0;
            if (!isOk){
                throw new MyDataException("删除操作失败：deleteById");
            }
        }else {
            throw new MyDataException("该部门存在子部门，不能直接删除");
        }
        return true;
    }


    /**
     * 把List<SysMenu> 转换为 List<MenuTableTreeVo>
     *
     * @param listSource List<SysMenu>数据
     * @param pid        父id
     * @return List<MenuTableTreeVo>数据
     */
    private List<MenuTableTreeVo> dealMenuTableTreeVoList(List<SysMenu> listSource, int pid) {
        List<MenuTableTreeVo> rList = new ArrayList<>();
        MenuTableTreeVo menuTableTreeVo = null;
        for (SysMenu menu : listSource) {
            if (menu.getParentId() == pid) {
                menuTableTreeVo = new MenuTableTreeVo();
                //复制SysMenu到MenuTableTreeVo
                menuTableTreeVo.setId(menu.getId());
                menuTableTreeVo.setParentId(menu.getParentId());
                menuTableTreeVo.setMenuName(menu.getMenuName());
                menuTableTreeVo.setMenuIcon(menu.getMenuIcon());
                menuTableTreeVo.setMenuUrl(menu.getMenuUrl());
                menuTableTreeVo.setMenuSort(menu.getMenuSort());
                menuTableTreeVo.setMenuType(menu.getMenuType());
                menuTableTreeVo.setMenuStatus(menu.getMenuStatus());
                menuTableTreeVo.setAuthorize(menu.getAuthorize());
                menuTableTreeVo.setRemark(menu.getRemark());

                //菜单类型(1目录 2页面 3按钮)
                if (menu.getMenuType() < 3) {
                    //1目录 2页面 查找子节点
                    List<MenuTableTreeVo> childList = dealMenuTableTreeVoList(listSource, menu.getId());
                    menuTableTreeVo.setTreeList(childList);
                } else {
                    //3按钮 无子节点
                    menuTableTreeVo.setTreeList(null);
                }
                //menuTableTreeVo 添加到list
                rList.add(menuTableTreeVo);
            }
        }
        return rList;
    }


    private List<TreeSelectVo> dealTreeSelect(List<SysMenu> listSource, int pid) {
        List<TreeSelectVo> rList = new ArrayList<>();
        TreeSelectVo treeSelectVo=null;
        for (SysMenu menu : listSource) {
            if (menu.getParentId()==pid){
                treeSelectVo=new TreeSelectVo();
                treeSelectVo.setId(menu.getId());//id
                treeSelectVo.setName(menu.getMenuName());//显示的值
                treeSelectVo.setChecked(false);
                treeSelectVo.setOpen(true);//默认展开

                //菜单类型(1目录 2页面 3按钮)
                if (menu.getMenuType()==1){
                    List<TreeSelectVo> children=this.dealTreeSelect(listSource,menu.getId());
                    if (children.size()>0){
                        //有子节点
                        treeSelectVo.setChildren(children);
                    }else {
                        //没有子节点
                        treeSelectVo.setChildren(null);
                    }
                }else {
                    treeSelectVo.setChildren(null);
                }

                rList.add(treeSelectVo);
            }
        }

        return rList;
    }

}
