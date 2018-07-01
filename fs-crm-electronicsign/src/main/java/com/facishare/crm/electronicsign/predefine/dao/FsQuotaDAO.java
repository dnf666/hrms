package com.facishare.crm.electronicsign.predefine.dao;

import com.facishare.crm.electronicsign.predefine.model.FsQuotaDO;
import org.mongodb.morphia.query.UpdateResults;

/**
 * 纷享配额数据库连接
 *
 * @author dailf
 */
public interface FsQuotaDAO extends BaseDao<FsQuotaDO> {
    /**
     * 查看纷享的配额信息
     *
     * @return 返回纷享的配额
     */
    FsQuotaDO getFsQuota();

    /**
     * 更新纷享的企业配额
     *
     * @param buyQuota 配额
     * @return 更新结果
     */
    int updateSaleEnterpriseQuota(int buyQuota, Long payMoney);

    /**
     * 更新纷享的个人配额
     *
     * @param buyQuota 配额
     * @return 更新结果
     */
    int updateSaleIndividualQuota(int buyQuota, Long payMoney);
}
