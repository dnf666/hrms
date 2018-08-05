package com.mis.hrm.util.demo.lambda;

import lombok.Data;

@Data
public class Members {
    private String name;
    private String sex;
    private String age;

    public Members(String name, String sex, String age){
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
}
