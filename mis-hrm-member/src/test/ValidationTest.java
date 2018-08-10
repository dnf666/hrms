import com.mis.hrm.member.config.ValidationConfig;
import com.mis.hrm.member.entity.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ValidationConfig.class)
public class ValidationTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    public void test(){
        Member member = new Member();
        member.setCompanyId("fdsfds");
        member.setSex("男");
        Objects.requireNonNull(validator);
        Set constraintViolations = validator.validate(member, Member.class);
        if (!constraintViolations.isEmpty()) {
            Iterator iterator = constraintViolations.iterator();
            //取第一个错误抛出
            ConstraintViolation constraintViolation = (ConstraintViolation) iterator.next();
            System.out.println(constraintViolation.getMessage());
        }
    }


}
