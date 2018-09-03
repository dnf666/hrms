package com.mis.hrm.work.service;

import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.Pager;
import com.mis.hrm.work.model.Whereabout;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkService extends BaseService<Whereabout> {
    //统计人员总数
    Long countWorkers();

    //分页获取已毕业成员的信息
    List<Whereabout> getAllGraduates(Pager<Whereabout> pager);

    //过滤
    List<Whereabout> filter(@Param("pager") Pager<Whereabout> pager,@Param("whereabout") Whereabout whereabout);


    //根据年级的分页查找
    List<Whereabout> findByGrade(@Param("pager") Pager<Whereabout> pager, @Param("grade") String grade);

    //根据姓名的模糊分页查找
    List<Whereabout> findByName(@Param("pager") Pager<Whereabout> pager,@Param("name") String name);
}
