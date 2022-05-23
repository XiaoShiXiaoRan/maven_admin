package com.gx.po;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_menu
 * @author 
 */
public class SysMenu implements Serializable {
    private Integer id;

    private Date gmtCreate;

    private Date gmtModified;

    /**
     * 父菜单Id(0表示是根菜单)
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
     * 菜单类型(1目录 2页面 3按钮)
     */
    private Byte menuType;

    /**
     * 菜单状态(0禁用 1启用)
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

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SysMenu other = (SysMenu) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getMenuName() == null ? other.getMenuName() == null : this.getMenuName().equals(other.getMenuName()))
            && (this.getMenuIcon() == null ? other.getMenuIcon() == null : this.getMenuIcon().equals(other.getMenuIcon()))
            && (this.getMenuUrl() == null ? other.getMenuUrl() == null : this.getMenuUrl().equals(other.getMenuUrl()))
            && (this.getMenuSort() == null ? other.getMenuSort() == null : this.getMenuSort().equals(other.getMenuSort()))
            && (this.getMenuType() == null ? other.getMenuType() == null : this.getMenuType().equals(other.getMenuType()))
            && (this.getMenuStatus() == null ? other.getMenuStatus() == null : this.getMenuStatus().equals(other.getMenuStatus()))
            && (this.getAuthorize() == null ? other.getAuthorize() == null : this.getAuthorize().equals(other.getAuthorize()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getMenuName() == null) ? 0 : getMenuName().hashCode());
        result = prime * result + ((getMenuIcon() == null) ? 0 : getMenuIcon().hashCode());
        result = prime * result + ((getMenuUrl() == null) ? 0 : getMenuUrl().hashCode());
        result = prime * result + ((getMenuSort() == null) ? 0 : getMenuSort().hashCode());
        result = prime * result + ((getMenuType() == null) ? 0 : getMenuType().hashCode());
        result = prime * result + ((getMenuStatus() == null) ? 0 : getMenuStatus().hashCode());
        result = prime * result + ((getAuthorize() == null) ? 0 : getAuthorize().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", gmtCreate=").append(gmtCreate);
        sb.append(", gmtModified=").append(gmtModified);
        sb.append(", parentId=").append(parentId);
        sb.append(", menuName=").append(menuName);
        sb.append(", menuIcon=").append(menuIcon);
        sb.append(", menuUrl=").append(menuUrl);
        sb.append(", menuSort=").append(menuSort);
        sb.append(", menuType=").append(menuType);
        sb.append(", menuStatus=").append(menuStatus);
        sb.append(", authorize=").append(authorize);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}