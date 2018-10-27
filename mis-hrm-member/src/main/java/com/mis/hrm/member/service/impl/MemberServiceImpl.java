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
import com.mis.hrm.work.dao.WorkMapper;
import com.mis.hrm.work.model.Whereabout;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = {})
public class MemberServiceImpl implements MemberService {
    private static final int MEMBER_PARAMTER_COUNT = 8;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MemberMapper memberMapper;
    @Resource
    private WorkMapper workMapper;
    @Override
    public int deleteByPrimaryKey(Member key) throws RuntimeException{
        if(StringUtil.notEmpty(key.getCompanyId()) && StringUtil.notEmpty(key.getNum())){
            int stateNum = memberMapper.deleteByPrimaryKey(key);
            if(stateNum > 0){
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
    public int insert(Member record) throws RuntimeException{
        if(StringUtil.notEmpty(record.getCompanyId()) && StringUtil.notEmpty(record.getNum())){
            int stateNum = memberMapper.insert(record);
            if(stateNum > 0){
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
    public Member selectByPrimaryKey(Member key) throws RuntimeException{
        Member member = memberMapper.selectByPrimaryKey(key);
        if(member != null){
            logger.info("成员信息查找成功");
            return member;
        } else {
            logger.info("成员不存在");
            throw new NullPointerException("成员不存在");
        }
    }

    @Override
    public int updateByPrimaryKey(Member record) throws RuntimeException{
        if(StringUtil.notEmpty(record.getCompanyId()) && StringUtil.notEmpty(record.getNum())){
            int stateNum = memberMapper.updateByPrimaryKey(record);
            if(stateNum > 0){
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
    public int deleteByNums(List<String> nums,String companyId) {
        if(!nums.equals(new ArrayList<>())){
            int stateNum = memberMapper.deleteByNums(nums,companyId);
            if(stateNum > 0){
                logger.info("成功删除" + stateNum + "名成员信息");
                return stateNum;
            } else {
                logger.info("成员信息删除失败");
                throw new RuntimeException("成员信息删除失败");
            }
        } else {
            logger.info("学号为空");
            throw new InfoNotFullyException("学号为空");
        }
    }

    @Override
    public Integer countMembers(Member member) {
        return memberMapper.countMembers(member);
    }

    @Override
    public List<Member> getAllMembers(Pager<Member> pager) {
        return memberMapper.getAllMembers(pager);
    }

    @Override
    public List<Member> filter(Pager<Member> pager, Member member) throws RuntimeException{
       Integer total = memberMapper.countMembersByKeys(member);
       pager.setPageTotal(total);
        if (ObjectNotEmpty.notEmpty(member)) {
            return memberMapper.filter(pager,member);
        } else {
            logger.info("未填写过滤条件");
            throw new InfoNotFullyException("未填写过滤条件");
        }
    }

    @Override
    public int exitToWhere(Whereabout whereabout) {
        String companyId = whereabout.getCompanyId();
        String num = whereabout.getNum();
        Member member = Member.builder().companyId(companyId).num(num).build();
        int result = memberMapper.deleteByPrimaryKey(member);
        logger.info("deletemember result {}",result);
        int res = workMapper.insert(whereabout);
        logger.info("insertworker result {}",res);
        return res;
    }

    @Override
    public void importMemberFromExcel(MultipartFile file) throws IOException {
        Sheet sheet = ExcelUtil.getSheet(file);
      List<Row> rows = ExcelUtil.getRowFromSheet(sheet);
      List<Member> list = new ArrayList<>();
      for (int i = 1;i<rows.size();i++){
          List<Cell> cells = ExcelUtil.getCellFromRow(rows.get(i));
          if (cells.size()!= MEMBER_PARAMTER_COUNT){
              throw new IOException(ErrorCode.MESSAGE_NOT_COMPLETE.getDescription());
          }
          //todo 没想到更好的方法。这段代码复用性太差。败笔啊
          String num =ExcelUtil.getStringByIndex(cells,0);
          String name = ExcelUtil.getStringByIndex(cells,1);
          String phoneNumber = ExcelUtil.getStringByIndex(cells,2);
          String email = ExcelUtil.getStringByIndex(cells,3);
          String grade = ExcelUtil.getStringByIndex(cells,4);
          String sex = ExcelUtil.getStringByIndex(cells,5);
          if (!Sex.judgeSex(sex)){
              throw new IOException("性别不合法");
          }
          String profession = ExcelUtil.getStringByIndex(cells,6);
          String department = ExcelUtil.getStringByIndex(cells,7);
          //todo companyId没传进来
          Member member = Member.builder().companyId("").num(num).name(name).phoneNumber(phoneNumber).email(email).grade(grade).sex(sex).profession(profession).department(department).build();
          logger.info("member {}",member.toString());
          list.add(member);
      }
        memberMapper.insertMany(list);

    }


}
