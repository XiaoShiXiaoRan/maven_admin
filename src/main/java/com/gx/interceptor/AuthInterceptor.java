package com.gx.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gx.dao.ISysMenuAuthorizeDao;
import com.gx.util.LoginSessionManager;
import com.gx.util.ProjectParameter;
import com.gx.util.Tools;
import com.gx.vo.JsonMsg;
import com.gx.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

public class AuthInterceptor implements HandlerInterceptor {
    //Jackson 序列化对象使用
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ISysMenuAuthorizeDao sysMenuAuthorizeDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断是否是Controller的方法
        if (handler instanceof HandlerMethod) {
            //获取方法的注解 获取了方法的url
            RequestMapping rm= ((HandlerMethod) handler).getMethodAnnotation(RequestMapping.class);
            String urlMethod=getUrlByRequestMapping(rm);
            //获取控制器的注解 - 获取了Controller的url
            RequestMapping rc=((HandlerMethod) handler).getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
            String urlC=getUrlByRequestMapping(rc);

            //获取请求的url
            String requestURI = request.getRequestURI(); // 获取访问的URI http://localhost:8080/baseAdmin/home
            //把主机:端口/项目名 替换掉  request.getContextPath()=http://localhost:8080/baseAdmin
            //strUrl=/home
            String strUrl = requestURI.replace(request.getContextPath(), "");
            // “”(首页) /(首页) /static（静态资源）  /home(主页面)  /login（登录页面）的不验证权限
            if (strUrl.equals("") || strUrl.equals("/") ||
                    strUrl.startsWith("/static") || strUrl.startsWith("/home") || strUrl.startsWith("/login")) {
                //允许通过本过滤器
                return true;
            } else {
                //获取Session
                HttpSession session = request.getSession();
                //登录时放入session的用户数据
                UserVo loginUser = (UserVo) session.getAttribute(ProjectParameter.SESSION_USER);
                //未登录 或 登录失效  从定向到 项目的根路径 --> 登录页
                if (loginUser == null) {
                    response.sendRedirect(request.getContextPath());
                } else {
                    //登录状态 其他设备登录检查
                    //获取记录中用户id对应的SessionID
                    String loginSessionId= LoginSessionManager.getInstance().getSessionIdByUserId(loginUser.getId());
                    //将记录中的sessionId和当前的SessionId比较
                    if (!loginSessionId.equals(session.getId())){
                        //在其他设备登录
                        //获取requestType 和 请求的contentType
                        String requestType = request.getHeader("X-Requested-With");
                        String contentType = request.getContentType() == null ? "" : request.getContentType().toLowerCase();
                        //判断是否是是Ajax请求
                        if (contentType.contains("application/json") ||
                                (requestType!=null && "XMLHttpRequest".equalsIgnoreCase(requestType.trim()))){
                            //ajax请求 返回Json格式的异常信息
                            //使用Response对象返回
                            response.setContentType("application/json;charset=utf-8");
                            JsonMsg jsonMsg=new JsonMsg();
                            jsonMsg.setCode(-1);
                            jsonMsg.setState(false);
                            jsonMsg.setMsg("您已经在其他设备登录了");

                            PrintWriter writer = response.getWriter();
                            writer.write(objectMapper.writeValueAsString(jsonMsg));
                            writer.flush();
                        }else {
                            //页面
                            //重定向到其他设备登录提示页面
                            response.sendRedirect(request.getContextPath() + "/home/otherDeviceLogin");
                        }
                        //移除session中记录的用户信息
                        session.removeAttribute(ProjectParameter.SESSION_USER);
                        return false;
                    }

                    //登录状态 权限检查
                    //获取method 参数
                    String doMehtod = urlMethod;//获取访问的方法名
                    System.err.println(strUrl + "; doMethod=" + doMehtod + "\tlogin Id=" + loginUser.getId() + ";roleId=" + loginUser.getRoleId());
                    if (doMehtod == null || "".equals(doMehtod)) {
                        //页面 执行index方法 - 判断 menu_url是否有权限
                        boolean hasAuth = this.sysMenuAuthorizeDao.hasRoleAuthByMenuUrl(loginUser.getRoleId(), strUrl) > 0;
                        if (hasAuth) {//有权限
                            return true;
                        } else {
                            //重定向到无权限页面
                            response.sendRedirect(request.getContextPath() + "/home/noAuth");
                            return false;
                        }
                    } else {
                        // 普通方法 增删查改
                        doMehtod = doMehtod.toLowerCase();//转换为小写字母
                        //处理分类
                        String methodType = "";
                        if (doMehtod.startsWith("select")) methodType = "select";
                        else if (doMehtod.startsWith("insert")) methodType = "insert";
                        else if (doMehtod.startsWith("update")) methodType = "update";
                        else if (doMehtod.startsWith("delete")) methodType = "delete";
                        else if (doMehtod.startsWith("export")) methodType = "export";
                        else if (doMehtod.startsWith("resetpassword")) methodType = "resetpassword";
                        if (!methodType.equals("")) {
                            //在控制范围的方法 /a/user   a/user
                            String authorize = urlC + ":" + methodType;
                            //调用dao的方法查询是否有权限
                            boolean hasAuth = this.sysMenuAuthorizeDao.hasRoleAuthByAuthorize(loginUser.getRoleId(), authorize) > 0;
                            if (hasAuth) {
                                return true;//通过权限检查
                            } else {
                                //返回错误信息
                                JsonMsg jsonMsg = new JsonMsg();
                                jsonMsg.setState(false);//失败
                                jsonMsg.setCode(-1);//无权限
                                jsonMsg.setMsg("您无权限访问");

                                response.setContentType("application/json;charset=UTF-8");
                                response.setCharacterEncoding("UTF-8");
                                //将jsonMsg序列化成json字符串返回
                                String strJsonMsg = objectMapper.writeValueAsString(jsonMsg);
                                PrintWriter out = response.getWriter();
                                out.write(strJsonMsg);
                                out.flush();
                                out.close();
                            }
                        } else {
                            //不在控制范围的方法，根据实际情况  这里直接放过
                            return true;
                        }
                    }
                }
            }

        }
        return true;
    }


    private String getUrlByRequestMapping(RequestMapping requestMapping){
        if (requestMapping==null){
            return null;
        }
        String[] values=requestMapping.value();
        if (values.length>0){
            if (Tools.isNotNull(values[0])){
                return values[0].replace("/","");
            }
        }
        return null;
    }
}
