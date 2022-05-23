package com.gx.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginSessionManager {

    private static final LoginSessionManager loginSessionManager = new LoginSessionManager();

    //存放 userId和sessionId的对应关系
    private final Map<Integer, String> loginSessionMap = new HashMap<>();


    //私有构造器
    private LoginSessionManager() {
    }

    public static LoginSessionManager getInstance() {
        return loginSessionManager;
    }

    /**
     * 记录用户ID和sessionID
     * @param userId 用户ID
     * @param sessionId sessionId
     */
    public void addSessionId(Integer userId, String sessionId) {
        //除该sessionId的记录，针对用户A登录后未退出直接用户B登录的情况
        removeBySessionId(sessionId);
        //记录当前的用户ID和SessionId
        String oldSessionId= loginSessionMap.put(userId,sessionId);
        if (oldSessionId!=null){
            System.out.println("userId="+userId+";sessionId="+oldSessionId+",在其他登录了");
        }
    }

    /**
     * 根据用户ID获取记录的SessionID
     * @param userId 用户ID
     * @return sessionID
     */
    public String getSessionIdByUserId(Integer userId){
        if (userId==null)return "";
        return loginSessionMap.getOrDefault(userId, "");
    }

    /**
     * 清除对应SessionID的所有记录
     * @param sessionId sessionId
     */
    public void removeBySessionId(String sessionId){
        System.out.println("移除sessionId相关记录，sessionId="+sessionId);
        List<Integer> removeKeys=new ArrayList<>();
        //遍历Map，值为当前SessionId的所有key
        for (Map.Entry<Integer, String> entry : loginSessionMap.entrySet()) {
            if (entry.getValue()!=null && entry.getValue().equals(sessionId)){
                removeKeys.add(entry.getKey());
            }
        }
        //遍历removeKeys 移除
        for (Integer key : removeKeys) {
            loginSessionMap.remove(key);
        }
    }
}
