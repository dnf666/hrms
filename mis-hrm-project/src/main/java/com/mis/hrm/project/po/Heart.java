package com.mis.hrm.project.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * created by dailf on 2018/12/29
 *
 * @author dailf
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Heart {
    private String url;
    private String status;
}
