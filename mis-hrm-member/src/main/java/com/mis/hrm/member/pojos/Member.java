package com.mis.hrm.member.pojos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    public String department;
}
