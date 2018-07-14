## 工程目的
基于web的人力资源管理

## 工程结构
Maven工程结构

```bash
mis-hrm
  └─mis-hrm-login 
  └─mis-hrm-member
  └─mis-hrm-book
  └─mis-hrm-project
  └─mis-hrm-index
  └─mis-hrm-manage
  └─mis-hrm-work   
```
模块说明
- mis-hrm-login 注册登录 
- mis-hrm-member 成员管理
- mis-hrm-book 图书管理
- mis-hrm-project 项目管理
- mis-hrm-index 管理系统主页
- mis-hrm-manage 管理员设置
- mis-hrm-work  人员去向

所有的工具类都写在mis-hrm-util。最好做成通用类
所有开发都在dev分支上开发
spring+springmvc+mybatis


