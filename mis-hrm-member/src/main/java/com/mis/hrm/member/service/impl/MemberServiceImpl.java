package com.mis.hrm.member.service.impl;

import com.mis.hrm.member.dao.MemberMapper;
import com.mis.hrm.member.model.Member;
import com.mis.hrm.member.service.MemberService;
import com.mis.hrm.util.ExcelUtil;
import com.mis.hrm.util.ObjectNotEmpty;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.enums.ErrorCode;
import com.mis.hrm.util.enums.Sex;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = {})
public class MemberServiceImpl implements MemberService {
    private static final int MEMBER_PARAMTER_COUNT = 9;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MemberMapper memberMapper;

    @Override
    public int deleteByPrimaryKey(Member key) throws RuntimeException {
        if (StringUtil.notEmpty(key.getCompanyId()) && StringUtil.notEmpty(key.getNum())) {
            int stateNum = memberMapper.deleteByPrimaryKey(key);
            if (stateNum > 0) {
                logger.info("成员信息删除成功");
                return stateNum;
            } else {
                logger.info("成员信息删除失败");
                throw new RuntimeException("成员信息删除失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public int insert(Member record) throws RuntimeException {
        if (StringUtil.notEmpty(record.getCompanyId()) && StringUtil.notEmpty(record.getNum())) {
            int stateNum = memberMapper.insert(record);
            if (stateNum > 0) {
                logger.info("成员信息添加成功");
                return stateNum;
            } else {
                logger.info("成员信息添加失败");
                throw new RuntimeException("成员信息添加失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public Member selectByPrimaryKey(Member key) throws RuntimeException {
        Member member = memberMapper.selectByPrimaryKey(key);
        if (member != null) {
            logger.info("成员信息查找成功");
            return member;
        } else {
            logger.info("成员不存在");
            throw new NullPointerException("成员不存在");
        }
    }

    @Override
    public int updateByPrimaryKey(Member record) throws RuntimeException {
        if (StringUtil.notEmpty(record.getCompanyId()) && StringUtil.notEmpty(record.getNum())) {
            int stateNum = memberMapper.updateByPrimaryKey(record);
            if (stateNum > 0) {
                logger.info("成员信息更新成功");
                return stateNum;
            } else {
                logger.info("成员信息更新失败");
                throw new RuntimeException("成员信息更新失败");
            }
        } else {
            logger.info("主键信息为空");
            throw new InfoNotFullyException("主键信息为空");
        }
    }

    @Override
    public List<Member> selectByPrimaryKeyAndPage(Member key, Pager<Member> pager) {
        return null;
    }

    @Override
    public int deleteByNums(List<String> nums, String companyId) {
        if (nums.size() != 0) {
            int stateNum = memberMapper.deleteByNums(nums, companyId);
            if (stateNum > 0) {
                logger.info("成功删除" + stateNum + "名成员信息");
                return stateNum;
            } else {
                logger.debug("成员信息删除失败");
                throw new RuntimeException("成员信息删除失败");
            }
        } else {
            logger.debug("学号为空");
            throw new InfoNotFullyException("学号为空");
        }
    }

    @Override
    public Integer countMembers(Member member) {
        return memberMapper.countMembers(member);
    }

    @Override
    public List<Member> filter(Pager<Member> pager, Member member) throws RuntimeException {
        Integer total = memberMapper.countMembersByKeys(member);
        pager.setRecordSize(total);
        if (ObjectNotEmpty.notEmpty(member)) {
            return memberMapper.filter(pager, member);
        } else {
            logger.info("未填写过滤条件");
            throw new InfoNotFullyException("未填写过滤条件");
        }
    }

    @Override
    public void importMemberFromExcel(MultipartFile file,String companyId) throws IOException {
        Sheet sheet = ExcelUtil.getSheet(file);
        List<Row> rows = ExcelUtil.getRowFromSheet(sheet);
        List<Member> list = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<Cell> cells = ExcelUtil.getCellFromRow(rows.get(i));
            if (cells.size() != MEMBER_PARAMTER_COUNT) {
                throw new IOException(ErrorCode.MESSAGE_NOT_COMPLETE.getDescription());
            }
            //todo 没想到更好的方法。这段代码复用性太差。败笔啊
            String num = ExcelUtil.getValueByIndex(cells, 0);
            String name = ExcelUtil.getValueByIndex(cells, 1);
            String phoneNumber = ExcelUtil.getValueByIndex(cells, 2);
            String email = ExcelUtil.getValueByIndex(cells, 3);
            String grade = ExcelUtil.getValueByIndex(cells, 4);
            String sex = ExcelUtil.getValueByIndex(cells, 5);
            if (!Sex.judgeSex(sex)) {
                throw new IOException("性别不合法");
            }
            String profession = ExcelUtil.getValueByIndex(cells, 6);
            String department = ExcelUtil.getValueByIndex(cells, 7);
            String whereAbout = ExcelUtil.getValueByIndex(cells, 8);
            Member member = Member.builder().companyId(companyId).num(num).name(name).phoneNumber(phoneNumber).email(email).grade(grade).sex(sex).profession(profession).department(department).whereAbout(whereAbout).build();
            logger.info("member {}", member.toString());
            list.add(member);
        }
        memberMapper.insertMany(list);

    }

    @Override
    public List<Member> selectByMultiKey(Member member) {
        return memberMapper.selectByMultiKey(member);
    }

    @Override
    public HSSFWorkbook exportExcel(List<Member> lists) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        sheet.addMergedRegion(new CellRangeAddress(0, lists.size() + 1, 0, 9));
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("学号");
        row0.createCell(1).setCellValue("姓名");
        row0.createCell(2).setCellValue("电话");
        row0.createCell(3).setCellValue("邮箱");
        row0.createCell(4).setCellValue("年级");
        row0.createCell(5).setCellValue("性别");
        row0.createCell(6).setCellValue("专业");
        row0.createCell(7).setCellValue("部门");
        row0.createCell(8).setCellValue("签约");
        for (int i = 0; i < lists.size(); i++) {
            Row row3 = sheet.createRow(i + 1);
            Member member1 = lists.get(i);
            row3.createCell(0).setCellValue(member1.getNum());
            row3.createCell(1).setCellValue(member1.getName());
            row3.createCell(2).setCellValue(member1.getPhoneNumber());
            row3.createCell(3).setCellValue(member1.getEmail());
            row3.createCell(4).setCellValue(member1.getGrade());
            row3.createCell(5).setCellValue(member1.getSex());
            row3.createCell(6).setCellValue(member1.getProfession());
            row3.createCell(7).setCellValue(member1.getDepartment());
            row3.createCell(8).setCellValue(member1.getWhereAbout());
        }
        logger.info("excel生成完毕");
        return workbook;
    }

}
