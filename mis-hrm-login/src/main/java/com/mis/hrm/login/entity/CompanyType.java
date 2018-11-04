package com.mis.hrm.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * created by dailf on 2018/11/4
 *
 * @author dailf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompanyType {
    private String majorType;
    private String viceType;

}
