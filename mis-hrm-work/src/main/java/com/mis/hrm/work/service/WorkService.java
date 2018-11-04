package com.mis.hrm.work.service;

import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.work.model.Whereabout;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkService extends BaseService<Whereabout> {
    /**
     *    批量删除
     */
    int deleteByNums(List<String> nums, String companyId);

    /**
     * 统计人数
     * @return
     */
    Long countWorkers();

    /**
     * 分页获取已毕业成员的信息
     * @param pager
     * @return
     */
    List<Whereabout> getAllGraduates(Pager<Whereabout> pager,String companyId);

    /**
     * 过滤
     * @param pager
     * @param whereabout
     * @return
     */
    List<Whereabout> filter(@Param("pager") Pager<Whereabout> pager,@Param("whereabout") Whereabout whereabout);
}
