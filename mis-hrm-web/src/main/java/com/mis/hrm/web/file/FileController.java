package com.mis.hrm.web.file;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.ServletContextResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;

/**
 * created by dailf on 2018/12/5
 *
 * @author dailf
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @ResponseBody
    @RequestMapping(value = "/download",method = RequestMethod.POST,produces = {"application/vnd.ms-excel;charset=UTF-8"})
    public ResponseEntity<byte[]> download(String name, HttpServletRequest req) {
        File file = null;
        try {
            Resource resource = new ServletContextResource(req.getServletContext(), "excel/" + name);
            file = resource.getFile();
        } catch (Exception e) {
            logger.info("获取文件错误", e);
            return null;
        }
        String fileName = new String(file.getName().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        HttpHeaders header = new HttpHeaders();
        header.setContentDispositionFormData("attachment", fileName);
        header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        byte[] res = null;
        try {
            res = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            logger.info("文件错误{0}", e);
            return null;
        }
        return new ResponseEntity<byte[]>(res, header, HttpStatus.CREATED);
    }


}
