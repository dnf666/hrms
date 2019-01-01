package com.mis.hrm.index.service.impl;

import com.mis.hrm.index.dao.IndexMapper;
import com.mis.hrm.index.entity.Index;
import com.mis.hrm.index.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.mis.hrm.util.ServiceUtil.checkSqlExecution;


/**
 * created by dailf on 2018/7/7
 * @author dailf
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private IndexMapper indexMapper;

    @Override
    public void deleteByPrimaryKey(Index key) {
        boolean flag = indexMapper.deleteByPrimaryKey(key);
        checkSqlExecution(flag);
    }

    @Override
    public void insert(Index record) {
        boolean flag = indexMapper.insert(record);
        checkSqlExecution(flag);
    }

    @Override
    public Index selectByPrimaryKey(Index key) {
        return indexMapper.selectByPrimaryKey(key);
    }

    @Override
    public void updateByPrimaryKey(Index record) {
        boolean flag = indexMapper.updateByPrimaryKey(record);
        checkSqlExecution(flag);
    }

    public void updatePhoto(Index index, MultipartFile file, HttpServletRequest request) throws IOException {
        String originalFilename = file.getOriginalFilename();
        Objects.requireNonNull(originalFilename);
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String fileName = System.currentTimeMillis() + suffix;

        savePhoto(file, request, fileName);

        index.setPhotoPath("/photo/" + fileName);
        indexMapper.updateByPrimaryKey(index);
    }

    private void savePhoto(MultipartFile file, HttpServletRequest request, String fileName) throws IOException {
        String filePath = request.getSession().getServletContext().getRealPath("/photo/" + fileName);
        checkFileExist(filePath);
        File saveFile = new File(filePath);
        try {
            file.transferTo(saveFile);
        }catch (IOException e){
            throw new IOException("文件上传失败");
        }
    }

    private void checkFileExist(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)){
            Files.createFile(path);
        }
    }
}
