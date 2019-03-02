package com.mis.hrm.login.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class Company {
    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email}")
    private String email;

    @NotBlank(message = "{name.notBlank}")
    @Length(min = 2, max = 10, message = "{name.length}")
    private String name;

    @NotBlank(message = "{applicantName.notBlank}")
    private String applicantName;

    @NotBlank(message = "{organizationSize.notBlank}")
    private String organizationSize;

    @NotBlank(message = "{mainCategory.notBlank}")
    private String mainCategory;

    @NotBlank(message = "{viceCategory.notBlank}")
    private String viceCategory;

    @NotBlank(message = "{password.notBlank}")
    @Length(min = 5, max = 20, message = "{password.length}")
    private String password;
}
