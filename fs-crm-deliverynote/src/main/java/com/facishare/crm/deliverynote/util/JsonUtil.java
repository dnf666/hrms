package com.facishare.crm.deliverynote.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;

public class JsonUtil {
    private static Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(JsonElement element, Type typeOfT) {
        return gson.fromJson(element, typeOfT);
    }

    public static JsonElement toJsonTree(Object obj) {
        return gson.toJsonTree(obj);
    }

    public static JsonElement parseToJsonObject(String obj) {
        JsonParser parser = new JsonParser();
        return parser.parse(obj).getAsJsonObject();
    }

    public static JsonObject toJsonObject(Object obj) {
        return gson.toJsonTree(obj).getAsJsonObject();
    }
}
