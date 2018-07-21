package com.mis.hrm.util.demo.lambda;

import lombok.Data;

@Data
public class Member {
    private String name;
    private String sex;
    private String age;

    public Member(String name,String sex,String age){
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
}
