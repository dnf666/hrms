package com.mis.hrm.manage.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * created on 2019-02-28
 * @author dailinfu
 */

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Management {
    private String companyId;
    private String email;
    private String password;
    /**
     * permission 1 管理员 2 成员
     */
    private Integer permission = 2;

}
