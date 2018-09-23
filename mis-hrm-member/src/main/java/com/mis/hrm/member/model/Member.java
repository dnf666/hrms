package com.mis.hrm.member.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private String companyId;
    private String num;
    private String name;
    private String phoneNumber;
    private String email;
    private String grade;
    private String sex;
    //专业
    private String profession;
    //部门
    private String department;

    public Member(String companyId,String num){
        this.companyId = companyId;
        this.num = num;
    }
}
