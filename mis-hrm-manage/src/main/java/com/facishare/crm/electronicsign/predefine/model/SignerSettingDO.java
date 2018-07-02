package com.facishare.crm.electronicsign.predefine.model;

import com.facishare.crm.electronicsign.enums.type.SignTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 签署者设置
 * Created by chenzs on 2018/5/3.
 */
@Data
public class SignerSettingDO implements Serializable {
    /**
     * @see SignTypeEnum
     */
    private Integer signType;		                //签署方式

    private String keyword;		                    //关键字
    private Integer orderNum;		                //序号
}
