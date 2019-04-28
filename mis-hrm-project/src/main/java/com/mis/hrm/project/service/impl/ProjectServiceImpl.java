package com.mis.hrm.project.service.impl;

import com.mis.hrm.project.dao.ProjectMapper;
import com.mis.hrm.project.po.Project;
import com.mis.hrm.project.po.Webapp;
import com.mis.hrm.project.service.ProjectService;
import com.mis.hrm.util.MailUtil;
import com.mis.hrm.util.Pager;
import com.mis.hrm.util.StringUtil;
import com.mis.hrm.util.exception.InfoNotFullyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectMapper projectMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public int deleteByPrimaryKey(Project key) throws InfoNotFullyException {
        try {
            Optional<Project> projectOptional = Optional.of(key);
        } catch (NullPointerException n) {
            logger.error("项目为空，删除失败");
            throw new NullPointerException("删除的prject对象为空");
        }
        if (!StringUtil.notEmpty(key.getCompanyId())) {
            logger.error("公司id不存在，删除失败");
            throw new InfoNotFullyException("公司id为空");
        }
        return projectMapper.deleteByPrimaryKey(key);
    }

    @Override
    public int insert(Project record) throws InfoNotFullyException {
        Optional<Project> projectOptional;
        try {
            projectOptional = Optional.of(record);
        } catch (NullPointerException n) {
            logger.error("项目为空，删除失败");
            throw new NullPointerException("删除的prject对象为空");
        }
        boolean isOk = projectOptional
                .filter(t -> t.baseRequired())
                .isPresent();
        if (!isOk) {
            logger.error("无法满足插入的基本条件");
            throw new InfoNotFullyException("插入的数据项条件缺失");
        }
        return projectMapper.insert(record);
    }

    @Override
    public Project selectByPrimaryKey(Project key) throws InfoNotFullyException {
        try {
            Optional<Project> projectOptional = Optional.of(key);
        } catch (NullPointerException n) {
            logger.error("项目为空，查找失败");
            throw new NullPointerException("传入的prject对象为空");
        }
        if (!StringUtil.notEmpty(key.getCompanyId())) {
            logger.error("公司id不存在，查询失败");
            throw new InfoNotFullyException("公司id为空");
        }
        return projectMapper.selectByPrimaryKey(key);
    }

    @Override
    public int updateByPrimaryKey(Project record) throws InfoNotFullyException {
        try {
            Optional<Project> projectOptional = Optional.of(record);
        } catch (NullPointerException n) {
            logger.error("项目为空，更新失败");
            throw new NullPointerException("传入的prject对象为空");
        }
        if (!StringUtil.notEmpty(record.getCompanyId())) {
            logger.error("公司id不存在，更新失败");
            throw new InfoNotFullyException("公司id为空");
        }
        return projectMapper.updateByPrimaryKey(record);
    }

    @Override
    public int getProjectCount(Project project) {
        return projectMapper.getProjectCount(project);
    }

    @Override
    public Integer deleteByProjectIds(List<Integer> numList, String companyId) {
        if (numList.size() != 0) {
            int stateNum = projectMapper.deleteByProjectIds(numList, companyId);
            if (stateNum > 0) {
                logger.info("成功删除" + stateNum + "信息");
                return stateNum;
            } else {
                logger.error("项目删除失败");
                throw new RuntimeException("项目删除失败");
            }
        } else {
            logger.debug("学号为空");
            throw new InfoNotFullyException("学号为空");
        }

    }

    @Override
    public boolean infoMember(List<String> emailList, String companyId, Integer projectId) throws Exception {
        if (emailList == null || emailList.size() == 0) {
            logger.error("成员列表为空");
            throw new Exception("成员列表为空");
        }
        if (companyId == null || projectId == null) {
            logger.error("公司id或项目id为空");
            throw new Exception("公司id或项目id为空");
        }
        Project project = Project.builder().companyId(companyId).projectId(projectId).build();
        Project project1 = projectMapper.selectByPrimaryKey(project);
        if (project1 != null) {
            logger.info("项目存在");
            String projectName = project1.getName();
            String projectUrl = project1.getIp();
            for (String email : emailList) {
                boolean result = MailUtil.projectWarning(email, projectName, projectUrl);
                if (result == false) {
                    logger.error("发送给{}的邮件失败", email);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Webapp> listProjectInTomcat(Project project) throws Exception {
        List<Webapp> webAppArrayList = new ArrayList<>();
        try {
            Integer.parseInt(project.getPorts());
        } catch (NumberFormatException e) {
            throw new Exception("端口不是数字");
        }
        String ip = project.getIp();
        String ports = project.getPorts();
        String url = "http://" + ip + ":" + ports + "/manager/text/list";
        String data = message(url);
        String[] oldDataStrs = data.split("/");
        if (oldDataStrs[0].startsWith("OK")) {
            //第一个字符串是描述，要忽略掉
            for (int i = 1; i < oldDataStrs.length; i++) {
                String[] names = oldDataStrs[i].split(":");//长度为4 0 ：忽略 1：状态 2：0 状态   3：名称
                Webapp webApp = new Webapp();
                webApp.setName(names[3]);
                if (names[1].equals("running")) {
                    if (oldDataStrs[i].split(":")[2].equals("0")) {
                        webApp.setStatus("运行");
                    } else {
                        webApp.setStatus("异常");
                    }
                } else if (names[1].equals("stopped")) {
                    if (oldDataStrs[i].split(":")[2].equals("0")) {
                        webApp.setStatus("停止");
                    } else {
                        webApp.setStatus("异常");
                    }
                } else {
                    webApp.setStatus("异常");
                }
                webAppArrayList.add(webApp);
            }
        }
        return webAppArrayList;
    }

    @Override
    public boolean operateProject(Project project, String operation) throws Exception {
        String ip = project.getIp();
        String ports = project.getPorts();
        String name = project.getName();
        String url = "http://" + ip + ":" + ports + "/manager/text/"+operation+"?path=/"+name;
        String data = message(url);
        if (data.startsWith("OK")) {
            return true;
        } else {
            throw new Exception("操作失败");
        }
    }

    public static String message(String operateURL) {
        StringBuffer dataResult = new StringBuffer();
        URL url = null;
        try {
            url = new URL(operateURL);
            URLConnection conn =  url.openConnection();
            /**
             * 这里的用户名和密码是由tomcat的%tomcat_home%/conf/tomcat-users.xml里所配置的用户
             */
            String username = "tomcat";
            String password = "tomcat";
            String configuration = username + ":" + password; // manager角色的用户
            String encodedPassword = new BASE64Encoder().encode(configuration.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedPassword);
            InputStream is = conn.getInputStream();
            BufferedReader bufreader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = bufreader.readLine()) != null) {
                dataResult.append(line);
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return dataResult.toString();
    }

    @Override
    public List<Project> selectByPrimaryKeyAndPage(Project project, Pager<Project> pager) throws Exception{
        int offset = pager.getOffset();
        int size = pager.getPageSize();
        int total = projectMapper.getCountByKeys(project);
        pager.setRecordSize(total);
        return projectMapper.selectByPrimaryKeyAndPage(project, offset, size);
    }
}
