package com.facishare.crm.electronicsign.predefine.dao.mapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by xialf on 2017/06/20.
 *
 * @author xialf
 * @since 2017/06/20 7:33 PM
 */
@Getter
@Setter
@ToString
public class EntityMapper<T> {
    private Class<T> clazz;
    private FieldInfo idField;
    private List<FieldInfo> fieldInfos;
}
