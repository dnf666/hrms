package com.facishare.crm.electronicsign.predefine.manager;

import com.facishare.crm.rest.TemplateApi;
import com.facishare.crm.rest.dto.RenderPdfModel;
import com.facishare.paas.appframework.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TemplatePdfManager {
    @Resource
    private TemplateApi templateApi;

    /**
     * 获取默认模板tnPath
     */
    public RenderPdfModel.Result getTemplatePdfTnPath(User user, String objDescApiName, String dataId, String orientation) {
        RenderPdfModel.Arg arg = new RenderPdfModel.Arg();
        arg.setObjDescApiName(objDescApiName);
        arg.setDataId(dataId);
        arg.setOrientation(orientation);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x-tenant-id", user.getTenantId());
        headers.put("x-user-id", user.getUserId());
        headers.put("Expect", "100-continue");
        return templateApi.renderPdf(arg, headers);
    }
}
