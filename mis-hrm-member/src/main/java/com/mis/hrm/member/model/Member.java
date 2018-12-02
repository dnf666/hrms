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
    private String profession;
    private String department;
    private String whereAbout;
    public Member(String companyId,String num){
        this.companyId = companyId;
        this.num = num;
    }
}
