package com.myfittinglife.typeadapterdemo.nulllist;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @Author LD
 * @Time 2022/6/20 14:37
 * @Describe Java方式实现一次
 * @Modify
 */
public class MyTypeAdapterFactory2 implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        Class<? super T> rawType = typeToken.getRawType();
        if (Collection.class.isAssignableFrom(rawType)) {
            Type elementType = $Gson$Types.getCollectionElementType(type, rawType);
            TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
            return new CollectionTypeAdapter2(elementTypeAdapter, typeToken);
        }
        return null;
    }
}
