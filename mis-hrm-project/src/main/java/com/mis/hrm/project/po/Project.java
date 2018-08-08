package com.mis.hrm.project.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project implements Serializable {
    private String companyId;
    private int projectId;
    private String projectName;
    private String projectUrl;
    private String onlineTime;
}
