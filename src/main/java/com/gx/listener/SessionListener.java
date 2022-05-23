package com.gx.listener;

import com.gx.util.LoginSessionManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

    private Logger logger=Logger.getLogger(SessionListener.class);

    //session创建时执行
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.warn("session创建，sessionId="+se.getSession().getId());
    }

    //session销毁时执行
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        logger.warn("session销毁，sessionId="+se.getSession().getId());
        LoginSessionManager.getInstance().removeBySessionId(se.getSession().getId());
    }
}
