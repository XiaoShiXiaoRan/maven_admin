package com.gx.controller;

import com.gx.annotation.ReturnJsonMapping;
import com.gx.exception.MyDataException;
import com.gx.po.SysRole;
import com.gx.service.IRoleService;
import com.gx.util.Tools;
import com.gx.vo.JsonMsg;
import com.gx.vo.LayuiTableData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/role")
public class RoleController {

    //service层
    @Autowired
    private IRoleService roleService;

    /**
     * 页面
     */
    @RequestMapping
    public String index() {
        return "/role";
    }

    /**
     * 查询分页数据 for layuiTable
     */
    @ReturnJsonMapping("/selectPageList")
    public LayuiTableData<SysRole> selectPageList(int page, int limit, String searchName,
                                                  @RequestParam(value = "searchStatus", required = false) Integer status) {
        return this.roleService.selectForPageList(page, limit, searchName, status);
    }

    /**
     * 新增
     */
    @ReturnJsonMapping("/insert")
    public JsonMsg insert(SysRole role) {
        JsonMsg jsonMsg = new JsonMsg();
        //数据验证
        if (Tools.isNotNull(role.getRoleName())) {
            if (role.getRoleSort() != null) {
                if (role.getRoleStatus() != null) {
                    //调用service层
                    try {
                        boolean ioOk = this.roleService.insert(role);
                        if (ioOk) {
                            jsonMsg.setState(true);
                            jsonMsg.setMsg("新增成功");
                        } else {
                            jsonMsg.setMsg("新增失败");
                        }
                    } catch (MyDataException e) {
                        jsonMsg.setMsg("新增失败");
                    }
                } else {
                    jsonMsg.setMsg("请选择角色状态");
                }
            } else {
                jsonMsg.setMsg("请输入正确的角色排序(整数)");
            }
        } else {
            jsonMsg.setMsg("请输入角色名称");
        }

        return jsonMsg;
    }

    /**
     * 根据id查询职位数据
     */
    @ReturnJsonMapping("/selectById")
    public JsonMsg selectById(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id != null && id > 0) {
            SysRole role = this.roleService.selectById(id);
            jsonMsg.setState(true);
            jsonMsg.setData(role);//通过jsonMsg data把数据返回
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

    /**
     * 修改
     */
    @ReturnJsonMapping("/update")
    public JsonMsg update(SysRole role) {
        JsonMsg jsonMsg = new JsonMsg();
        //数据验证
        if (role.getId() != null && role.getId() > 0) {
            if (Tools.isNotNull(role.getRoleName())) {
                if (role.getRoleSort() != null) {
                    if (role.getRoleStatus() != null) {
                        //调用service层
                        try {
                            boolean ioOk = this.roleService.update(role);
                            if (ioOk) {
                                jsonMsg.setState(true);
                                jsonMsg.setMsg("修改成功");
                            } else {
                                jsonMsg.setMsg("修改失败");
                            }
                        } catch (MyDataException e) {
                            jsonMsg.setMsg("修改失败");
                        }
                    } else {
                        jsonMsg.setMsg("请选择角色状态");
                    }
                } else {
                    jsonMsg.setMsg("请输入正确的角色排序(整数)");
                }
            } else {
                jsonMsg.setMsg("请输入角色名称");
            }
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

    /**
     * 根据id删除职位数据
     */
    @ReturnJsonMapping("/deleteById")
    public JsonMsg deleteById(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id != null && id > 0) {
            try {
                boolean isOk = this.roleService.deleteById(id);
                if (isOk){
                    jsonMsg.setState(true);
                    jsonMsg.setMsg("删除成功");
                }
            } catch (MyDataException e) {
                // e.printStackTrace();
                jsonMsg.setMsg(e.getMessage());
            }
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

    /**
     * 查询授权的菜单数据 for layui Tree
     */
    @ReturnJsonMapping("/selectMenuForLayuiTree")
    public JsonMsg selectMenuForLayuiTree(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id != null && id > 0) {
            jsonMsg.setState(true);
            jsonMsg.setData(this.roleService.selectMenuForLayuiTree(id));
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

    /**
     * 角色授权
     */
    @ReturnJsonMapping("/updateRoleAuthorize")
    public JsonMsg updateRoleAuthorize(Integer roleId,
                                       @RequestParam(value = "selectMenuIds", required = false) String strSelectMenuIdsAll) {
        JsonMsg jsonMsg = new JsonMsg();
        if (roleId != null && roleId > 0) {
            List<Integer> selectMenuIdList = new ArrayList<>();
            //分割字符串
            String[] strSelectMenuIds = strSelectMenuIdsAll.split(",");
            //遍历转换
            for (String strSelectMenuId : strSelectMenuIds) {
                if (Tools.isInteger(strSelectMenuId)) {
                    selectMenuIdList.add(Integer.parseInt(strSelectMenuId));
                }
            }
            //调用服务端 进行权限授权
            jsonMsg = this.roleService.authorize(roleId, selectMenuIdList);
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

}
