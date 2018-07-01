package com.facishare.crm.customeraccount.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.facishare.crm.customeraccount.entity.CustomerAccountConfig;
import com.github.mybatis.mapper.ICrudMapper;

/**
 * 
 */
public interface CustomerAccountConfigDao extends ICrudMapper<CustomerAccountConfig> {

    @Select("select * from customer_account_config where tenant_id = #{tenantId}")
    CustomerAccountConfig findByTenantId(@Param("tenantId") String tenantId);

    @Select("select * from customer_account_config where customer_account_enable = 1")
    List<CustomerAccountConfig> findEnterpriseWithCustomerAccountOpening();

    @Select("<script>select * from customer_account_config where customer_account_enable = 1 and tenant_id in <foreach item='item' collection='tenantIds' open='(' close=')' separator=','>#{item}</foreach></script>")
    List<CustomerAccountConfig> findEnterpriseWithCustomerAccountOpeningByTenantIds(@Param("tenantIds") List<String> tenantIds);

    @Select("select * from customer_account_config where customer_account_enable=#{customerAccountEnable}")
    List<CustomerAccountConfig> list(int customerAccountEnable);

}
