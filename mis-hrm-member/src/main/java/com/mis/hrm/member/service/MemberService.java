package com.mis.hrm.member.service;

import com.mis.hrm.member.model.Member;
import com.mis.hrm.util.BaseService;
import com.mis.hrm.util.Pager;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MemberService extends BaseService<Member> {
    /**
     * 批量删除
     * @param nums 学号
     * @param companyId 公司id
     * @return 删除的数目
     */
    int deleteByNums(List<String> nums,String companyId);

    /**
     * 根据条件统计成员
     * @param member 条件
     * @return 成员数
     */
    Integer countMembers(Member member);

    /**
     *  过滤
     */
    List<Member> filter(Pager<Member> pager, Member member);

    /**
     * 导入成员
     * @param file 成员excel
     * @param companyId 公司id
     * @throws IOException 异常
     */
    void importMemberFromExcel(MultipartFile file,String companyId) throws IOException;

    /**
     * 多条件筛选
     * @param member 条件
     * @return 成员集合
     */
    List<Member> selectByMultiKey(Member member);

    /**
     * 导出成员
     * @param lists 要导出的成员
     * @return excel对象
     */
    HSSFWorkbook exportExcel(List<Member> lists);

    List<Member> selectByCompanyId(Member member);
}
