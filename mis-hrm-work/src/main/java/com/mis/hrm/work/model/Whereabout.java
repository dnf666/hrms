package com.mis.hrm.work.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Whereabout {
    private String companyId;
    private String num;
    private String name;
    private String phoneNumber;
    private String email;
    private String grade;
    private String sex;
    private String profession;
    private String department;
    private String workPlace;

    public Whereabout(String companyId,String num){
        this.companyId = companyId;
        this.num = num;
    }
}
