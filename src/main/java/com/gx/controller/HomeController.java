package com.gx.controller;

import com.gx.service.IMenuService;
import com.gx.util.ProjectParameter;
import com.gx.vo.MenuTableTreeVo;
import com.gx.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {
    //service
    @Autowired
    private IMenuService menuService;

    @RequestMapping
    public String index(HttpSession session, Model model, HttpServletResponse response){
        UserVo loginUser= (UserVo) session.getAttribute(ProjectParameter.SESSION_USER);
        if (loginUser!=null){
            //登录
            int roleId=loginUser.getRoleId();
            List<MenuTableTreeVo> listMenu=this.menuService.selectMenuByRoleId(roleId);
            //把菜单通过request传到页面
            model.addAttribute("listMenu",listMenu);
            model.addAttribute("loginUser",loginUser);
            return "/home";
        }else {
            //未登录
            //重定向 到项目的根路径 跳转到login页面
            // try {
            //     response.sendRedirect("/");//直接重定向到 web容器（Tomcat）的根路径
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }
            // return null;
            return "redirect:/";
        }
    }

    @RequestMapping("/welcome")
    public String welcome(){
        return "/welcome";
    }

    /**
     * 无权限显示的页面
     */
    @RequestMapping("/noAuth")
    public String noAuth(){
        return "/noAuth";
    }

    @RequestMapping("/otherDeviceLogin")
    public String otherDeviceLogin(){
        return "/otherDeviceLogin";
    }
}
