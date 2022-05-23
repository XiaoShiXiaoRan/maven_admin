package com.gx.vo;

import java.io.Serializable;
import java.util.List;

public class MenuTableTreeVo implements Serializable {
    private static final long serialVersionUID = 4921918287122115656L;

    private Integer id;

    /**
     * 父菜单id
     */
    private Integer parentId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 菜单url
     */
    private String menuUrl;

    /**
     * 菜单排序
     */
    private Integer menuSort;

    /**
     * 菜单类型
     */
    private Byte menuType;

    /**
     * 菜单状态
     */
    private Byte menuStatus;

    /**
     * 权限标识
     */
    private String authorize;

    /**
     * 备注
     */
    private String remark;

    /**
     * 子菜单集合
     */
    private List<MenuTableTreeVo> treeList;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public Integer getMenuSort() {
        return menuSort;
    }

    public void setMenuSort(Integer menuSort) {
        this.menuSort = menuSort;
    }

    public Byte getMenuType() {
        return menuType;
    }

    public void setMenuType(Byte menuType) {
        this.menuType = menuType;
    }

    public Byte getMenuStatus() {
        return menuStatus;
    }

    public void setMenuStatus(Byte menuStatus) {
        this.menuStatus = menuStatus;
    }

    public String getAuthorize() {
        return authorize;
    }

    public void setAuthorize(String authorize) {
        this.authorize = authorize;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<MenuTableTreeVo> getTreeList() {
        return treeList;
    }

    public void setTreeList(List<MenuTableTreeVo> treeList) {
        this.treeList = treeList;
    }
}
