package com.mis.hrm.util;

import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * created by dailf on 2018/7/28
 *
 * @author dailf
 */
@Slf4j
public class EncryptionUtilTest {
    private String password = "123456";
    private String emptyPassword = null;

    @Test
    public void md5() {
       String newPassword =  EncryptionUtil.md5(password);
       EncryptionUtil.md5("");
       log.info(newPassword);
    }

    @Test
    public void sha1() {
    }

    @Test
    public void mac() {
    }

    @Test
    public void encode() {
    }

    @Test
    public void encode1() {
    }
}