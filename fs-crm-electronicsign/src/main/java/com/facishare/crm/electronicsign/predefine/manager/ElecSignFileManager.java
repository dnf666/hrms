package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.electronicsign.exception.ElecSignBusinessException;
import com.facishare.crm.electronicsign.exception.ElecSignErrorCode;
import com.facishare.fsi.proxy.model.warehouse.n.fileupload.NDownloadFile;
import com.facishare.fsi.proxy.model.warehouse.n.fileupload.NUploadFileDirect;
import com.facishare.fsi.proxy.service.NFileStorageService;
import com.facishare.paas.appframework.core.model.User;
import com.lowagie.text.pdf.PdfReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class ElecSignFileManager {
    @Resource
    private NFileStorageService nFileStorageService;

    /**
     * 上传到文件系统
     * http://git.firstshare.cn/Qixin/fs-warehouse/wikis/n/NUploadFileDirectAction
     */
    public NUploadFileDirect.Result nUploadFile(String ea, Integer userId, byte[] data, String fileExt) {
        NUploadFileDirect.Arg arg = new NUploadFileDirect.Arg();
        arg.setEa(ea);
        arg.setSourceUser("E." + userId);
        arg.setData(data);
        arg.setFileExt(fileExt);

        NUploadFileDirect.Result result = null;
        try {
            result = nFileStorageService.nUploadFileDirect(arg, ea);
            log.debug("nFileStorageService.nUploadFileDirect success. arg[{}], result[{}]", arg, result);  //data太大，不打了
            log.info("nFileStorageService.nUploadFileDirect success. ea[{}], userId[{}], fileExt[{}], result[{}]", ea, userId, fileExt, result);
            return result;
        } catch (Exception e) {
            log.debug("nFileStorageService.nUploadFileDirect failed. arg[{}], result[{}]", arg, result, e);
            log.warn("nFileStorageService.nUploadFileDirect failed. ea[{}], userId[{}], fileExt[{}], result[{}]", ea, userId, fileExt, result, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.UPLOAD_FILE_TO_FILE_SYSTEM_FAILED, ElecSignErrorCode.UPLOAD_FILE_TO_FILE_SYSTEM_FAILED.getMessage() + e);
        }
    }

    /**
     * 下载
     * http://git.firstshare.cn/Qixin/fs-warehouse/wikis/n/NDownloadFileAction
     */
    public byte[] download(String ea, String userId, String tnPath) {
        NDownloadFile.Arg arg = new NDownloadFile.Arg();
        arg.setEa(ea);
        arg.setnPath(tnPath);
        arg.setDownloadUser("E." + userId);

        NDownloadFile.Result result = null;
        try {
            result = nFileStorageService.nDownloadFile(arg, ea);  // 问过付杰，这个接口可以下载tnPath的
            log.info("nFileStorageService.nDownloadFile success. arg[{}]", arg);
            log.debug("nFileStorageService.nDownloadFile success. arg[{}], result[{}]", arg, result);
            return result.getData();
        } catch (Exception e) {
            log.warn("nFileStorageService.nDownloadFile failed. arg[{}], result[{}]", arg, result, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.DOWN_OBJ_TEMPLATE_FAILED, ElecSignErrorCode.DOWN_OBJ_TEMPLATE_FAILED.getMessage() + e);
        }
    }

    /**
     * 获取pdf文件的页数
     */
    public int getTotalPageNum(User user, byte[] pdf) {
        PdfReader reader= null;
        try {
            reader = new PdfReader(pdf);
            return reader.getNumberOfPages();
        } catch (IOException e) {
            log.warn("getTotalPageNum failed. user[{}]", user, e);
            log.debug("getTotalPageNum failed. user[{}], pdf[{}]", user, pdf, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.GET_PDF_TOTAL_PAGE_NUM_FAILED, ElecSignErrorCode.GET_PDF_TOTAL_PAGE_NUM_FAILED.getMessage() + e);
        } catch (Exception e) {
            log.warn("getTotalPageNum failed. user[{}]", user, e);
            log.debug("getTotalPageNum failed. user[{}], pdf[{}]", user, pdf, e);
            throw new ElecSignBusinessException(ElecSignErrorCode.GET_PDF_TOTAL_PAGE_NUM_FAILED, ElecSignErrorCode.GET_PDF_TOTAL_PAGE_NUM_FAILED.getMessage() + e);
        }
    }
}
