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
    private String name;
    private String ip;
    private String ports;
    private String location;
    /**
     * @return 满足基本条件　？　true:false
     */
    public boolean baseRequired() {
        return StringUtil.notEmpty(companyId, name, ip, ports);
    }
}
