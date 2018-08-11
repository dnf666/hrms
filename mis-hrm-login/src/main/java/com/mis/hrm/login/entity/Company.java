package com.mis.hrm.login.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author May
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Company对象")
public class Company {
    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email}")
    private String email;

    @NotBlank(message = "{name.notBlank}")
    @Length(min = 2, max = 10, message = "{name.length}")
    @ApiModelProperty(value = "公司名称",required = true)
    private String name;

    @NotBlank(message = "applicantName.notBlank")
    @ApiModelProperty(value = "申请者姓名",required = true)
    private String applicantName;

    @NotBlank(message = "organizationSize.notBlank")
    @ApiModelProperty(value = "组织规模",required = true)
    private String organizationSize;

    @NotBlank(message = "mainCategory.notBlank")
    @ApiModelProperty(value = "主分类",required = true)
    private String mainCategory;

    @NotBlank(message = "viceCategory.notBlank")
    @ApiModelProperty(value = "副分类",required = true)
    private String viceCategory;

    @NotBlank(message = "password.notBlank")
    @Length(min = 5, max = 20, message = "{password.length}")
    private String password;
}
