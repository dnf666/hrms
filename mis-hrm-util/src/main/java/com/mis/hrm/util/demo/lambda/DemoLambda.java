package com.mis.hrm.util.demo.lambda;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DemoLambda {
    public static void main(String[] args) {
        //创建成员集合
        List<Member> members = Arrays.asList(
                new Member("刘岽","男","12"),
                new Member("刘墩","男","15"),
                new Member("刘dong","男","18"),
                new Member("墩儿","女","20")
        );

        //从集合中获取成员姓名并形成新集合
        List<String> memberNames = members.stream().map(Member::getName).collect(Collectors.toList());
        //遍历新集合
        memberNames.forEach(System.out::println);
    }
}