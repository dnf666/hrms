package com.mis.hrm.work.pojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
