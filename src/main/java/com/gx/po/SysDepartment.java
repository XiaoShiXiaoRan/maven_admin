package com.gx.po;

import java.io.Serializable;
import java.util.Date;

public class SysDepartment implements Serializable {
    private static final long serialVersionUID = 1990891460439931136L;
    /*
        id int NOT NULL
    gmt_create datetime NULL
    gmt_modified datetime NULL
    parent_id int NULL父部门id(0表示是根部门)
    department_name varchar(50) NULL部门名称
    telephone varchar(20) NULL部门电话
    fax varchar(20) NULL部门传真
    email varchar(30) NULL部门Email
    principal varchar(30) NULL部门负责人
    department_sort int NULL部门排序
    remark varchar(100) NULL备注
         */
    private Integer id;
    private Date gmtCreate;
    private Date gmtModified;
    private Integer parentId;
    private String departmentName;
    private String telephone;
    private String fax;
    private String email;
    private String principal;
    private Integer departmentSort;
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Integer getDepartmentSort() {
        return departmentSort;
    }

    public void setDepartmentSort(Integer departmentSort) {
        this.departmentSort = departmentSort;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "sysDepartment{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", parentId=" + parentId +
                ", departmentName='" + departmentName + '\'' +
                ", telephone='" + telephone + '\'' +
                ", fax='" + fax + '\'' +
                ", email='" + email + '\'' +
                ", principal='" + principal + '\'' +
                ", departmentSort=" + departmentSort +
                ", remark='" + remark + '\'' +
                '}';
    }
}
