package com.gx.controller;

import com.gx.annotation.ReturnJsonMapping;
import com.gx.exception.MyDataException;
import com.gx.po.SysPosition;
import com.gx.service.IPositionService;
import com.gx.util.Tools;
import com.gx.vo.JsonMsg;
import com.gx.vo.LayuiTableData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

@Controller
@RequestMapping("/position")
public class PositionController {

    //service层
    @Autowired
    private IPositionService positionService;

    /**
     * 页面
     */
    @RequestMapping
    public String index() {
        return "/position";
    }

    /**
     * 查询分页数据 for layuiTable
     */
    // @RequestMapping(value = "/selectPageList", produces = "application/json;charset=utf-8")
    // @ResponseBody
    @ReturnJsonMapping("/selectPageList")
    public LayuiTableData<SysPosition> selectPageList(int page, int limit, String searchName,
                                                      @RequestParam(value = "searchStatus", required = false) Integer status) {
        return this.positionService.selectForPageList(page, limit, searchName, status);
    }

    /**
     * 新增时获取下一排序的序号
     */
    // @RequestMapping(value = "/selectNextSort", produces = "application/json;charset=utf-8")
    // @ResponseBody
    @ReturnJsonMapping("/selectNextSort")
    public JsonMsg selectNextSort() {
        //调用service获取当前的总行数
        int count = this.positionService.countAll();
        JsonMsg jsonMsg = new JsonMsg();
        jsonMsg.setState(true);
        jsonMsg.setData(count + 1);//下一序号=当前数据条数+1
        return jsonMsg;
    }


    /**
     * 新增
     */
    @RequestMapping(value = "/insert", produces = "application/json;charset=utf-8")
    @ResponseBody
    public JsonMsg insert(SysPosition position) {
        JsonMsg jsonMsg = new JsonMsg();
        //数据验证
        if (Tools.isNotNull(position.getPositionName())) {
            if (position.getPositionSort() != null) {
                if (position.getPositionStatus() != null) {
                    //调用service层
                    try {
                        boolean ioOk = this.positionService.insert(position);
                        if (ioOk) {
                            jsonMsg.setState(true);
                            jsonMsg.setMsg("新增成功");
                        }else {
                            jsonMsg.setMsg("新增失败 else");
                        }
                    } catch (MyDataException e) {
                        e.printStackTrace();
                        jsonMsg.setMsg("新增失败");
                    }
                } else {
                    jsonMsg.setMsg("请选择职位状态");
                }
            } else {
                jsonMsg.setMsg("请输入正确的职位排序(整数)");
            }
        } else {
            jsonMsg.setMsg("请输入职位名称");
        }
        return jsonMsg;
    }

    /**
     * 根据id查询职位数据
     */
    @RequestMapping(value = "/selectById", produces = "application/json;charset=utf-8")
    @ResponseBody
    public JsonMsg selectById(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id != null && id > 0) {
            SysPosition position = this.positionService.selectById(id);
            jsonMsg.setState(true);
            jsonMsg.setData(position);//通过jsonMsg data把数据返回
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update", produces = "application/json;charset=utf-8")
    @ResponseBody
    public JsonMsg update(SysPosition position) {
        JsonMsg jsonMsg = new JsonMsg();
        //数据验证
        if (position.getId() != null && position.getId() > 0) {
            if (Tools.isNotNull(position.getPositionName())) {
                if (position.getPositionSort() != null) {
                    if (position.getPositionStatus() != null) {
                        //调用service层
                        try {
                            boolean ioOk = this.positionService.update(position);
                            if (ioOk) {
                                jsonMsg.setState(true);
                                jsonMsg.setMsg("新增成功");
                            }
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            jsonMsg.setMsg("新增失败");
                        }
                    } else {
                        jsonMsg.setMsg("请选择职位状态");
                    }
                } else {
                    jsonMsg.setMsg("请输入正确的职位排序(整数)");
                }
            } else {
                jsonMsg.setMsg("请输入职位名称");
            }
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }

    /**
     * 根据id删除职位数据
     */
    @RequestMapping(value = "/deleteById", produces = "application/json;charset=utf-8")
    @ResponseBody
    public JsonMsg deleteById(Integer id) {
        JsonMsg jsonMsg = new JsonMsg();
        if (id != null && id > 0) {
            try {
                jsonMsg = this.positionService.deleteById(id);
            } catch (RuntimeException e) {
                e.printStackTrace();
                jsonMsg.setMsg("删除失败");
            }
        } else {
            jsonMsg.setMsg("参数异常");
        }
        return jsonMsg;
    }
}
