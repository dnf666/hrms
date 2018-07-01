package com.facishare.crm.electronicsign.mongo.spring;

import com.github.fakemongo.Fongo;
import com.github.mongo.support.DatastoreExt;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.DatastoreImpl;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MapperOptions;
import org.springframework.beans.factory.FactoryBean;

/**
 * mongodb测试使用的factory bean.
 * Created by zenglb on 2016/8/22.
 */
public class MongoDataStoreTestFactoryBean implements FactoryBean<Datastore> {
    private Datastore datastore;
    private final Fongo fongo = new Fongo("javaJunitMongo");

    public MongoDataStoreTestFactoryBean(String dbName, String packageName) throws Exception {
        Morphia morphia = new Morphia();
        MapperOptions options = morphia.mapPackage(packageName, true).getMapper().getOptions();
        options.setStoreEmpties(false);
        options.setStoreNulls(false);
        DatastoreImpl datastoreImpl = new DatastoreImpl(morphia,morphia.getMapper(),new MongoClient(){
            @Override
            public DB getDB(String dbName) {
                return fongo.getDB(dbName);
            }
        }, dbName);
        datastore =  datastoreImpl;
    }

    @Override
    public Class<DatastoreExt> getObjectType() {
        return DatastoreExt.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Datastore getObject() {
        return datastore;
    }
}
