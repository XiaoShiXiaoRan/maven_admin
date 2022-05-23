package com.gx.controller;

import com.gx.annotation.ReturnJsonMapping;
import com.gx.exception.MyDataException;
import com.gx.po.SysDepartment;
import com.gx.service.IDepartmentService;
import com.gx.util.Tools;
import com.gx.vo.DepartmentTableTreeVo;
import com.gx.vo.JsonMsg;
import com.gx.vo.LayuiTableData;
import com.gx.vo.TreeSelectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/department")
public class DepartmentController{

    //service
    @Autowired
    private IDepartmentService departmentService;

    @RequestMapping
    public String index() {
        return "/department";
    }

    /**
     *查询数据 for table tree
     */
    @ReturnJsonMapping("/selectPageList")
    public LayuiTableData<DepartmentTableTreeVo> selectPageList() {
        return this.departmentService.selectForTable();
    }

    /**
     * 根据父id 查询下一序号
     */
    @ReturnJsonMapping("/selectNextSort")
    public JsonMsg selectNextSort(Integer pid) {
        int nextSort=this.departmentService.countAllByPid(pid)+1;
        //返回
        JsonMsg msg=new JsonMsg();
        msg.setState(true);
        msg.setData(nextSort);
        return msg;
    }

    /**
     * 查询部门数据 for 树形下拉框
     */
    @ReturnJsonMapping("/SelectForTreeSelect")
    public List<TreeSelectVo> SelectForTreeSelect() {
        return this.departmentService.SelectForTreeSelect();
    }

    /**
     * 新增部门
     */
    @ReturnJsonMapping("/insert")
    public JsonMsg insert(SysDepartment department){
        JsonMsg jsonMsg = new JsonMsg();
        //数据验证
        if (department.getParentId()!=null) {
            if (Tools.isNotNull(department.getDepartmentName())) {
                if (department.getDepartmentSort()!=null) {
                    //新增
                   try {
                       boolean isOk = this.departmentService.insert(department);
                       if (isOk) {
                           jsonMsg.setState(true);
                           jsonMsg.setMsg("新增成功！");
                       } else {
                           jsonMsg.setMsg("新增失败！");
                       }
                   }catch (MyDataException e){
                       jsonMsg.setMsg("新增失败！");
                   }
                } else {
                    jsonMsg.setMsg("请输入显示排序");
                }
            } else {
                jsonMsg.setMsg("请输入部门名称");
            }
        } else {
            jsonMsg.setMsg("请选择上级部门");
        }
        return jsonMsg;
    }


    /**
     * 查询部门数据 by id
     */
    @ReturnJsonMapping("/selectById")
    public JsonMsg selectById(Integer id){
        JsonMsg jsonMsg = new JsonMsg();
        if (id!=null && id>0){
            jsonMsg.setState(true);
            jsonMsg.setData(this.departmentService.selectById(id));
        }else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }


    /**
     * 修改部门
     */
    @ReturnJsonMapping("/update")
    public JsonMsg update(SysDepartment department){
        JsonMsg jsonMsg = new JsonMsg();
        //数据验证
        if (department.getId()!=null && department.getId()>0){
            if (department.getParentId()!=null) {
                if (Tools.isNotNull(department.getDepartmentName())) {
                    if (department.getDepartmentSort()!=null) {
                        //新增
                       try {
                           boolean isOk = this.departmentService.update(department);
                           if (isOk) {
                               jsonMsg.setState(true);
                               jsonMsg.setMsg("修改成功！");
                           } else {
                               jsonMsg.setMsg("修改失败！");
                           }
                       }catch (MyDataException e){
                           jsonMsg.setMsg("修改失败！");
                       }
                    } else {
                        jsonMsg.setMsg("请输入显示排序");
                    }
                } else {
                    jsonMsg.setMsg("请输入部门名称");
                }
            } else {
                jsonMsg.setMsg("请选择上级部门");
            }
        }else{
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

    /**
     * 删除部门
     */
    @ReturnJsonMapping("/deleteById")
    public JsonMsg deleteById(Integer id){
        JsonMsg jsonMsg = new JsonMsg();
        if (id!=null && id>0){
            //删除
            try {
                this.departmentService.deleteById(id);
                jsonMsg.setState(true);
                jsonMsg.setMsg("删除成功");
            } catch (MyDataException e) {
                // e.printStackTrace();
                jsonMsg.setMsg(e.getMessage());
            }
        }else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

}