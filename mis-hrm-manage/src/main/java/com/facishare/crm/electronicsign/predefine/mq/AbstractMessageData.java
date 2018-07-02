package com.facishare.crm.electronicsign.predefine.mq;

import com.alibaba.fastjson.JSON;

public abstract class AbstractMessageData {
    public byte[] toMessageData() {
        return JSON.toJSONBytes(this);
    }
}
