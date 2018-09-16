package com.mis.hrm.util.demo.file;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public class DemoFileUpload {

    @RequestMapping("fileUpload")
    public void fileUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws FileEmptyException {
        //若文件不为空
        if (!file.isEmpty()) {
            try {
                //设置保存路径(当前服务器端物理位置 / upload / (当前时间戳+上传时的文件名).jpg )
                String filePath = request.getSession().getServletContext().getRealPath("/") + "upload/"
                        + System.currentTimeMillis() + file.getOriginalFilename();
                //转存文件
                file.transferTo(new File(filePath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new FileEmptyException("该文件为空！");
        }
    }

}
