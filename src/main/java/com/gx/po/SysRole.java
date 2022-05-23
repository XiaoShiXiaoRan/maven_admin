package com.gx.po;

import java.io.Serializable;
import java.util.Date;

public class SysRole implements Serializable {

    private static final long serialVersionUID = -1303093551824659461L;
    /*
id int NOT NULL
gmt_create datetime NULL
gmt_modified datetime NULL
role_name varchar(20) NULL角色名称
role_sort int NULL角色排序
role_status tinyint NULL角色状态(0禁用 1启用)
remark varchar(50) NULL备注
     */
    /**
     * 主键
     */
    private Integer id;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 最后修改时间
     */
    private Date gmtModified;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色排序
     */
    private Integer roleSort;
    /**
     * 角色状态(0禁用 1启用)
     */
    private Byte roleStatus;
    /**
     * 备注
     */
    private String remark;

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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(Integer roleSort) {
        this.roleSort = roleSort;
    }

    public Byte getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(Byte roleStatus) {
        this.roleStatus = roleStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "SysRole{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", roleName='" + roleName + '\'' +
                ", roleSort=" + roleSort +
                ", roleStatus=" + roleStatus +
                ", remark='" + remark + '\'' +
                '}';
    }
}
