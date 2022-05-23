package com.gx.controller;

import com.gx.annotation.ReturnJsonMapping;
import com.gx.exception.MyDataException;
import com.gx.po.SysMenu;
import com.gx.service.IMenuService;
import com.gx.util.Tools;
import com.gx.vo.JsonMsg;
import com.gx.vo.LayuiTableData;
import com.gx.vo.MenuTableTreeVo;
import com.gx.vo.TreeSelectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/menu")
public class MenuController {
    //service层
    @Autowired
    private IMenuService menuService;

    @RequestMapping
    public String index() {
        return "/menu";
    }

    /**
     * 查询数据 for table tree
     */
    @ReturnJsonMapping("/selectPageList")
    public LayuiTableData<MenuTableTreeVo> selectPageList() {
        return this.menuService.selectForTable();
    }

    /**
     * 根据父id 查询下一序号
     */
    @ReturnJsonMapping("/selectNextSort")
    public JsonMsg selectNextSort(Integer pid) {
        if (pid == null) pid = 0;
        int nextSort = this.menuService.countAllByPid(pid) + 1;
        //返回
        JsonMsg msg = new JsonMsg();
        msg.setState(true);
        msg.setData(nextSort);
        return msg;
    }

    /**
     * 查询菜单数据 for 树形下拉框
     */
    @ReturnJsonMapping("/SelectForTreeSelect")
    public List<TreeSelectVo> SelectForTreeSelect() {
        return this.menuService.SelectForTreeSelect();
    }

    /**
     * 新增菜单
     */
    @ReturnJsonMapping("/insert")
    public JsonMsg insert(SysMenu menu) {
        JsonMsg jsonMsg = new JsonMsg();
        //数据验证
        if (menu.getParentId() != null) {
            if (Tools.isNotNull(menu.getMenuName())) {
                if (menu.getMenuSort() != null) {
                    if (menu.getMenuType() != null) {
                        if (menu.getMenuStatus() != null) {
                            //新增
                            try {
                                boolean isOk = this.menuService.insert(menu);
                                if (isOk) {
                                    jsonMsg.setState(true);
                                    jsonMsg.setMsg("新增成功！");
                                } else {
                                    jsonMsg.setMsg("新增失败！");
                                }
                            } catch (MyDataException e) {
                                jsonMsg.setMsg("新增失败！");
                            }
                        } else {
                            jsonMsg.setMsg("请选择菜单状态");
                        }
                    } else {
                        jsonMsg.setMsg("请选择菜单类型");
                    }
                } else {
                    jsonMsg.setMsg("请输入显示排序");
                }
            } else {
                jsonMsg.setMsg("请输入菜单名称");
            }
        } else {
            jsonMsg.setMsg("请选择上级菜单");
        }
        return jsonMsg;
    }

    /**
     * 查询菜单数据 by id
     */
    @ReturnJsonMapping("/selectById")
    public JsonMsg selectById(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id != null && id > 0) {
            jsonMsg.setState(true);
            jsonMsg.setData(this.menuService.selectById(id));
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

    /**
     * 修改菜单
     */
    @ReturnJsonMapping("/update")
    public JsonMsg update(SysMenu menu) {
        JsonMsg jsonMsg = new JsonMsg();
        //数据验证
        if (menu.getId() != null && menu.getId() > 0) {
            if (menu.getParentId() != null) {
                if (Tools.isNotNull(menu.getMenuName())) {
                    if (menu.getMenuSort() != null) {
                        if (menu.getMenuType() != null) {
                            if (menu.getMenuStatus() != null) {
                                //修改
                                try {
                                    boolean isOk = this.menuService.update(menu);
                                    if (isOk) {
                                        jsonMsg.setState(true);
                                        jsonMsg.setMsg("修改成功！");
                                    } else {
                                        jsonMsg.setMsg("修改失败！");
                                    }
                                } catch (MyDataException e) {
                                    jsonMsg.setMsg("修改失败！");
                                }
                            } else {
                                jsonMsg.setMsg("请选择菜单状态");
                            }
                        } else {
                            jsonMsg.setMsg("请选择菜单类型");
                        }
                    } else {
                        jsonMsg.setMsg("请输入显示排序");
                    }
                } else {
                    jsonMsg.setMsg("请输入菜单名称");
                }
            } else {
                jsonMsg.setMsg("请选择上级菜单");
            }
        } else {
            jsonMsg.setMsg("非法访问");
        }
        return jsonMsg;
    }

    /**
     * 删除菜单
     */
    @ReturnJsonMapping("/deleteById")
    public JsonMsg deleteById(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id != null && id > 0) {
            //删除
            try {
                this.menuService.deleteById(id);
                jsonMsg.setState(true);
                jsonMsg.setMsg("删除成功");
            } catch (MyDataException e) {
                // e.printStackTrace();
                jsonMsg.setMsg(e.getMessage());
            }
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }
}
