package com.mis.hrm.work.dao;

import com.mis.hrm.util.BaseMapper;
import com.mis.hrm.util.Pager;
import com.mis.hrm.work.model.Whereabout;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkMapper extends BaseMapper<Whereabout> {
    //批量删除
    int deleteByNums(List<String> nums,String companyId);

    //统计人员总数
    Long countWorkers();

    //分页获取已毕业成员的信息
    List<Whereabout> getAllGraduates(Pager<Whereabout> pager,String companyId);

    //过滤
    List<Whereabout> filter(@Param("pager") Pager<Whereabout> pager,@Param("whereabout") Whereabout whereabout);
}
