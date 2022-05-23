package com.gx.service.impl;

import com.gx.dao.ISysUserDao;
import com.gx.service.ILoginService;
import com.gx.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("loginService")
@Transactional
public class LoginServiceImpl implements ILoginService {
    // dao
    @Autowired
    private ISysUserDao userDao;

    @Override
    public UserVo selectUserByName(String userName) {
        //直接调用dao的方法
        return this.userDao.selectUserByName(userName);
    }
}
