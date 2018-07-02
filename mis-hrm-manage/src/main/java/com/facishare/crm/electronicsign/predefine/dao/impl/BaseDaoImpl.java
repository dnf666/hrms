package com.facishare.crm.electronicsign.predefine.dao.impl;

import com.facishare.crm.electronicsign.predefine.dao.BaseDao;
import com.facishare.crm.electronicsign.predefine.dao.mapper.EntityMapper;
import com.facishare.crm.electronicsign.predefine.dao.mapper.EntityMapperManager;
import com.facishare.crm.electronicsign.predefine.dao.mapper.FieldInfo;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class BaseDaoImpl<T> implements BaseDao<T> {
    private Datastore datastore;
    private Class<T> clazz;

    public BaseDaoImpl(Datastore datastore, Class<T> clazz) {
        this.datastore = datastore;
        this.clazz = clazz;
    }

    @Override
    public String save(T entity) {
        return datastore.save(entity).getId().toString();
    }

    @Override
    public List<T> queryList(T condition) {
        final Query<T> query = createQuery(condition);
        return query.asList();
    }

    @Override
    public List<T> queryList(T condition, int offset, int limit) {
        final Query<T> query = createQuery(condition);
        query.limit(limit);
        query.offset(offset);
        return query.asList();
    }

    @Override
    public T queryById(String id) {
        final Query<T> query = createQuery();
        query.field("_id").equal(new ObjectId(id));
        return query.get();
    }

    @Override
    public List<T> queryByIds(List<String> ids) {
        final Query<T> query = createQuery();
        List<ObjectId> objectIds = ids.stream().map(ObjectId::new).collect(Collectors.toList());
        query.field("_id").in(objectIds);
        return query.asList();
    }

    @Override
    public long queryCount(T condition) {
        final Query<T> query = createQuery(condition);
        return datastore.getCount(query);
    }

    @Override
    public Datastore getDatastore() {
        return this.datastore;
    }

    @Override
    public Query<T> createQuery() {
        return datastore.createQuery(clazz);
    }

    @Override
    public UpdateOperations<T> createUpdateOperations() {
        return datastore.createUpdateOperations(clazz);
    }

    public Query<T> createQuery(T condition) {
        final EntityMapper<T> entityMapper = EntityMapperManager.INSTANCE.getEntityMapper(clazz);
        final Query<T> query = createQuery();
        try {
            final String id = (String) entityMapper.getIdField().getGetterMethod().invoke(condition);
            if (id != null) {
                query.field("_id").equal(new ObjectId(id));
            }

            for (final FieldInfo fieldInfo : entityMapper.getFieldInfos()) {
                if (!fieldInfo.getFieldName().equals(entityMapper.getIdField().getFieldName())) {
                    final Object fieldValue = fieldInfo.getGetterMethod().invoke(condition);
                    if (fieldValue instanceof String) {
                        if (!Strings.isNullOrEmpty((String) fieldValue)) {
                            query.field(fieldInfo.getFieldName()).equal((String) fieldValue);
                        }
                    } else {
                        if (null != fieldValue) {
                            query.field(fieldInfo.getFieldName()).equal(fieldValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return query;
    }

    public Query<T> createQuery(T condition, int offset, int limit) {
        Query<T> query = createQuery(condition);
        query.offset(offset);
        query.limit(limit);
        return query;
    }
}