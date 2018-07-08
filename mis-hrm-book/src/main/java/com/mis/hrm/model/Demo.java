package com.mis.hrm.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * created by dailf on 2018/7/7
 *
 * @author dailf
 */
@Data
@Builder
@NoArgsConstructor
@ToString
public class Demo {
    private String name;
    private String password;
}
