package com.mis.hrm.index.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author May
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Index {
    private String companyId;
    private String outline;
    private String photoPath;
}
