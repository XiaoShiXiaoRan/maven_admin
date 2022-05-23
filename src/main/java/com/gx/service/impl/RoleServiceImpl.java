package com.gx.service.impl;

import com.gx.dao.ISysMenuAuthorizeDao;
import com.gx.dao.ISysMenuDao;
import com.gx.dao.ISysRoleDao;
import com.gx.dao.ISysUserDao;
import com.gx.exception.MyDataException;
import com.gx.po.SysMenu;
import com.gx.po.SysMenuAuthorize;
import com.gx.po.SysRole;
import com.gx.service.IRoleService;
import com.gx.vo.JsonMsg;
import com.gx.vo.LayuiTableData;
import com.gx.vo.LayuiTreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("roleService")
@Transactional
public class RoleServiceImpl implements IRoleService {
    //dao
    @Autowired
    private ISysRoleDao roleDao ;
    @Autowired
    private ISysUserDao userDao;
    @Autowired
    private ISysMenuDao menuDao;
    @Autowired
    private ISysMenuAuthorizeDao menuAuthorizeDao;

    @Override
    public LayuiTableData<SysRole> selectForPageList(int page, int limit, String searchName, Integer status) {
        List<SysRole> data = this.roleDao.selectForPageList(page, limit, searchName, status);
        int count = this.roleDao.countAll(searchName, status);
        return new LayuiTableData<>(count, data);
    }

    @Override
    public int countAll() {
        return this.roleDao.countAll(null, null);
    }

    @Override
    public SysRole selectById(int id) {
        return this.roleDao.selectById(id);
    }

    @Override
    public boolean insert(SysRole role) {
        //查询理论下一序号
        int nextSort=this.countAll()+1;
        //传入的序号 小于 nextSort，用户调小的自动生成的序号
        if (nextSort> role.getRoleSort()){
            int r= this.roleDao.updateSortPlus1(role.getRoleSort(),null);
            System.out.println("r="+r);
        }
        //新增
        boolean isOk=this.roleDao.insert(role)>0;
        if (!isOk){
            throw new MyDataException("新增操作失败: "+role);
        }
        return true;
    }

    @Override
    public boolean update(SysRole role) {
        //查询出未修改的数据
        SysRole dbRole=this.roleDao.selectById(role.getId());
        if (dbRole.getRoleSort()>role.getRoleSort()){
            //序号向前（小）移动    大于等于新的序号，小于旧的序号 +1
            this.roleDao.updateSortPlus1(role.getRoleSort(), dbRole.getRoleSort()-1);
        }else{
            //序号向后（大）移动    大于旧的序号，小于等于新的序号  -1
            this.roleDao.updateSortMinus1(dbRole.getRoleSort()+1, role.getRoleSort());
        }
        //修改
        boolean isOk=this.roleDao.update(role)>0;
        if (!isOk){
            throw new RuntimeException("修改操作失败: "+role);
        }
        return true;
    }

    @Override
    public boolean deleteById(int id){
        //查询准备删除的职位是否在使用
        int useCount = this.userDao.countUserByRoleId(id);
        if (useCount == 0) {
            SysRole dbRole=this.roleDao.selectById(id);
            //更新序号
            this.roleDao.updateSortMinus1(dbRole.getRoleSort()+1,null);
            //执行删除
            boolean isOk=this.roleDao.deleteById(id)>0;
            if (!isOk){
                throw new MyDataException("删除操作失败："+id);
            }
        } else {
            //使用中 不能删除
            throw new MyDataException("该角色使用中，不能删除");
        }
        return true;
    }

    @Override
    public List<LayuiTreeVo> selectMenuForLayuiTree(int roleId) {
        // // 所有的菜单
        List<SysMenu> menuList = this.menuDao.selectAll();
        //查询出当前角色已经授权的菜单的id list
        List<Integer> authMenuIds = this.menuAuthorizeDao.selectMenuIdByRoleId(roleId);
        return dealMenuForLayuiTree(menuList,authMenuIds,0);
    }

    @Override
    public JsonMsg authorize(int roleId, List<Integer> selectMenuIdList) {
        JsonMsg jsonMsg = new JsonMsg();
        //根据 角色id 查询出已经授权的的权限(菜单)id
        List<Integer> oldAuth = this.menuAuthorizeDao.selectMenuIdByRoleId(roleId);
        // 找出新增的权限 在新的list中，不在旧的list中的
        List<Integer> addIdList = new ArrayList<>();
        for (Integer id : selectMenuIdList) {
            if (!oldAuth.contains(id)) {
                addIdList.add(id);
            }
        }
        // 找出 移除的权限 在旧的list中 ，不在新的list中的
        List<Integer> removeList = new ArrayList<>();
        for (Integer id : oldAuth) {
            if (!selectMenuIdList.contains(id)) {
                removeList.add(id);
            }
        }

        //数据处理
        int count=0;
        SysMenuAuthorize sysMenuAuthorize=null;
        //遍历添加
        for(Integer addMenuId:addIdList){
            sysMenuAuthorize=new SysMenuAuthorize();
            sysMenuAuthorize.setRoleId(roleId);//角色id
            sysMenuAuthorize.setMenuId(addMenuId);//菜单id
            count+=this.menuAuthorizeDao.insert(sysMenuAuthorize);
        }
        if (count!=addIdList.size()){
            throw new MyDataException("遍历添加 失败，总添加条数："+addIdList.size()+"；成功："+count);
        }
        //遍历移除
        count=0;
        for(Integer removeMenuId:removeList){
            count+=this.menuAuthorizeDao.deleteById(roleId,removeMenuId);
        }
        if (count!=removeList.size()){
            throw new MyDataException("遍历移除 失败，总移除条数："+removeList.size()+"；成功："+count);
        }

        jsonMsg.setState(true);
        jsonMsg.setMsg("授权成功");
        return jsonMsg;
    }


    /**
     * 处理菜单 for layui Tree
     * @param listSource 所有菜单list
     * @param authMenuIds 该角色已经授权的菜单id list
     * @param pid 父id
     * @return
     */
    private List<LayuiTreeVo> dealMenuForLayuiTree(List<SysMenu> listSource, List<Integer> authMenuIds, int pid) {
        List<LayuiTreeVo> rList = new ArrayList<>();
        LayuiTreeVo layuiTreeVo=null;
        for (SysMenu menu : listSource) {
            if (menu.getParentId()==pid){
                layuiTreeVo=new LayuiTreeVo();
                layuiTreeVo.setId(menu.getId());
                layuiTreeVo.setTitle(menu.getMenuName());
                layuiTreeVo.setSpread(false);//默认为折叠状态

                List<LayuiTreeVo> listChildren=dealMenuForLayuiTree(listSource,authMenuIds,menu.getId());
                //layui tree 如果勾选父节点 layui Tree默认会勾选所有的子节点，哪怕子节点未勾选
                if (listChildren.size()>0){
                    //有子节点的不能checked
                    layuiTreeVo.setChildren(listChildren);
                }else {
                    layuiTreeVo.setChildren(null);
                    //么有子节点的 checked
                    //判断该节点的id 是否在 已经勾选菜单id list中，如在，就勾选
                    if (authMenuIds.contains(menu.getId())){
                        layuiTreeVo.setChecked(true);//勾选
                    }
                }

                rList.add(layuiTreeVo);
            }
        }

        return rList;
    }
}

