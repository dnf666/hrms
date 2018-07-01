package com.facishare.crm.customeraccount.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by xujf on 2017-11-11.
 */
@Getter
@Setter
@ToString
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "tenant_id")
    private String tenantId;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "update_by")
    private String updateBy;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;

    public void init(String createBy) {
        this.createBy = createBy;
        this.updateBy = createBy;
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public void init() {
        this.createTime = new Date();
        this.updateTime = new Date();
    }
}
