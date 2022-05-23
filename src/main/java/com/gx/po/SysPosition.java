package com.gx.po;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_position
 * @author 
 */
public class SysPosition implements Serializable {
    private Integer id;

    private Date gmtCreate;

    private Date gmtModified;


    /**
     * 职位名称
     */
    private String positionName;

    /**
     * 职位排序
     */
    private Integer positionSort;

    /**
     * 职位状态(0禁用 1启用)
     */
    private Byte positionStatus;

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

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Integer getPositionSort() {
        return positionSort;
    }

    public void setPositionSort(Integer positionSort) {
        this.positionSort = positionSort;
    }

    public Byte getPositionStatus() {
        return positionStatus;
    }

    public void setPositionStatus(Byte positionStatus) {
        this.positionStatus = positionStatus;
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
        SysPosition other = (SysPosition) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGmtCreate() == null ? other.getGmtCreate() == null : this.getGmtCreate().equals(other.getGmtCreate()))
            && (this.getGmtModified() == null ? other.getGmtModified() == null : this.getGmtModified().equals(other.getGmtModified()))
            && (this.getPositionName() == null ? other.getPositionName() == null : this.getPositionName().equals(other.getPositionName()))
            && (this.getPositionSort() == null ? other.getPositionSort() == null : this.getPositionSort().equals(other.getPositionSort()))
            && (this.getPositionStatus() == null ? other.getPositionStatus() == null : this.getPositionStatus().equals(other.getPositionStatus()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGmtCreate() == null) ? 0 : getGmtCreate().hashCode());
        result = prime * result + ((getGmtModified() == null) ? 0 : getGmtModified().hashCode());
        result = prime * result + ((getPositionName() == null) ? 0 : getPositionName().hashCode());
        result = prime * result + ((getPositionSort() == null) ? 0 : getPositionSort().hashCode());
        result = prime * result + ((getPositionStatus() == null) ? 0 : getPositionStatus().hashCode());
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
        sb.append(", positionName=").append(positionName);
        sb.append(", positionSort=").append(positionSort);
        sb.append(", positionStatus=").append(positionStatus);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}