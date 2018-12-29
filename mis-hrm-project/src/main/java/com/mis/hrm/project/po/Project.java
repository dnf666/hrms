package com.mis.hrm.project.po;

import com.mis.hrm.util.StringUtil;
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
    private Integer projectId;
    private String projectName;
    private String projectUrl;
    private String onlineTime;
    private String status;
    /**
     *
     * @return 满足基本条件　？　true:false
     */
    public boolean baseRequired(){
        return StringUtil.notEmpty(companyId, projectName, projectUrl, onlineTime);
    }
}
