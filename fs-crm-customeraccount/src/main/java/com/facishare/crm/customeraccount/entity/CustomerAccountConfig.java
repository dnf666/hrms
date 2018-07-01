package com.facishare.crm.customeraccount.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "customer_account_config")
@Data
public class CustomerAccountConfig extends BaseEntity {

    @Column(name = "credit_enable")
    private boolean creditEnable;
    @Column(name = "customer_account_enable")
    private int customerAccountEnable;
}