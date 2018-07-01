package com.facishare.crm.customeraccount.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "customer_account_bill")
@Data
public class CustomerAccountBill extends BaseEntity {
    private static final long serialVersionUID = -4239231803790445233L;
    @Column(name = "customer_account_id")
    private String customerAccountId;
    @Column(name = "relate_id")
    private String relateId;
    @Column(name = "prepay_amount_change")
    private double prepayAmountChange;
    @Column(name = "prepay_locked_amount_change")
    private double prepayLockedAmountChange;
    @Column(name = "rebate_amount_change")
    private double rebateAmountChange;
    @Column(name = "rebate_locked_amount_change")
    private double rebateLockedAmountChange;
    @Column(name = "bill_date")
    private Date billDate;
    @Column(name = "is_repair")
    private Boolean repair;
    @Column(name = "remark")
    private String remark;

}
