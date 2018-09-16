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


注意：
1.后端每天开发前必须先pull master 的代码。
2.无论是修复bug还是添加功能 均创建一个新分支。命名格式 ddmmyy_操作_对象 如 150918_delete_druid
3. 当你的代码测试通过后，可以提交到master分支(提交前再次pull master分支的代码)。我会merge 你们的分支。 