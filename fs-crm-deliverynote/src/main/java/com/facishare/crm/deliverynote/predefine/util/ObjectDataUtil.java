package com.facishare.crm.deliverynote.predefine.util;

import com.facishare.crm.deliverynote.predefine.model.DeliveryNoteProductVO;
import com.facishare.paas.appframework.metadata.ObjectDataExt;
import com.facishare.paas.metadata.api.IObjectData;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ObjectDataUtil {

    public static  <T> T parseObjectData(IObjectData iObjectData, Class<T> targetClass) {
        try {
//            // 数字或金额字段转换（因为关联字段值为空时，元数据会返回“N/A”，见com.facishare.paas.metadata.util.JsonFieldHandle#bulkHandleQuoteField）
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(BigDecimal.class, new TypeAdapter<BigDecimal>() {
                        @Override
                        public void write(JsonWriter out, BigDecimal value) throws IOException {
                            out.value(String.valueOf(value));
                        }

                        @Override
                        public BigDecimal read(JsonReader in) throws IOException {
                            try {
                                return new BigDecimal(in.nextString());
                            } catch (NumberFormatException e) {
                                return null;
                            }
                        }
                    })
                    .create();


            return gson.fromJson(ObjectDataExt.of(iObjectData).toJsonString(), targetClass);
        } catch (Exception e) {
            log.error("iObjectData[{}], targetClass[{}]", iObjectData, targetClass, e);
            throw new RuntimeException(e);
        }
    }

    public static  <T> List<T> parseObjectData(List<IObjectData> iObjectDataList, Class<T> targetClass) {
        if (CollectionUtils.isEmpty(iObjectDataList)) {
            return new ArrayList<>(0);
        }
        return iObjectDataList
                .stream().map(iObjectData -> parseObjectData(iObjectData, targetClass))
                .collect(Collectors.toList());
    }

}
