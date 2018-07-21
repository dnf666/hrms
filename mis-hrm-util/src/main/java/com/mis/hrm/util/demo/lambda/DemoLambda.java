package com.mis.hrm.util.demo.lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DemoLambda {
    public static void main(String[] args) {
        //创建成员集合
        List<Member> members = Arrays.asList(
                new Member("刘岽","男","12"),
                new Member("刘墩","男","15"),
                new Member("刘dong","男","18"),
                new Member("墩儿","女","20")
        );
        //创建成员姓名的集合
        List<String> memberNames = new ArrayList<>();

        //从集合中获取成员姓名形成新集合
        members.forEach(member -> memberNames.add(member.getName()));
        //使用lambda表达式遍历集合
        memberNames.forEach(memberName -> System.out.println(memberName));
    }
}