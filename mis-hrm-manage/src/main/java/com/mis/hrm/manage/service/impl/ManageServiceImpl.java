package com.mis.hrm.manage.service.impl;

import com.mis.hrm.manage.dao.ManageMapper;
import com.mis.hrm.manage.model.Management;
import com.mis.hrm.manage.service.ManageService;
import com.mis.hrm.util.EncryptionUtil;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import javax.annotation.Resource;

/**
 * created on 2019-02-28
 *
 * @author dailinfu
 */

@Service
public class ManageServiceImpl implements ManageService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private ManageMapper manageMapper;

    @Override
    public int deleteByPrimaryKey(Management key) throws InfoNotFullyException {
        return 0;
    }

    /**
     * 初始密码 并添加登录权限
     *
     * @param record 记录
     * @return 成功
     * @throws InfoNotFullyException
     */
    @Override
    public int insert(Management record) throws InfoNotFullyException {
        Integer base_password = 123456;
        String encrypt = EncryptionUtil.md5(base_password.toString());
        record.setPassword(encrypt);
        return manageMapper.insert(record);
    }

    @Override
    public Management selectByPrimaryKey(Management key) throws InfoNotFullyException {
        String originPassword = key.getPassword();
        String encrpt = EncryptionUtil.md5(originPassword);
        Management management2 = manageMapper.selectByPrimaryKey(key);
        String originPassword2 = management2.getPassword();
        if (!encrpt.equals(originPassword2)) {
            throw new InfoNotFullyException("密码错误");
        } else {
            return management2;
        }
    }

    @Override
    public int updateByPrimaryKey(Management record) throws InfoNotFullyException {
        if (record.getPassword() != null) {
            String password = record.getPassword();
            String encrypt = EncryptionUtil.md5(password);
            record.setPassword(encrypt);
        }
        return manageMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<Management> selectByPrimaryKeyAndPage(Management key, Pager<Management> pager) {
        return null;
    }
}
