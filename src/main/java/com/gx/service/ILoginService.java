package com.gx.service;

import com.gx.vo.UserVo;

/**
 * 登录 服务层接口
 */
public interface ILoginService {
    /**
     * 根据用户名查找用户数据 (login)
     * @param userName 用户名
     * @return 用户数据
     */
    UserVo selectUserByName(String userName);
}
