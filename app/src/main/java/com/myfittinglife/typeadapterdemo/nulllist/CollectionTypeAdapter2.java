package com.myfittinglife.typeadapterdemo.nulllist;

import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

/**
 * @Author LD
 * @Time 2022/6/20 14:24
 * @Describe Java方式实现一次
 * @Modify
 */
class CollectionTypeAdapter2<T> extends TypeAdapter<Collection<T>> {

    private TypeAdapter<T> elementTypeAdapter;
    private TypeToken<T> typeToken;
    /*
       这里有疑问，Java的TypeToken可以写T，但Kotlin不行，得写另一种泛型E，不然显示类型冲突，为什么？
     */
    public CollectionTypeAdapter2(TypeAdapter<T> elementTypeAdapter, TypeToken<T> typeToken) {
        this.elementTypeAdapter = elementTypeAdapter;
        this.typeToken = typeToken;
    }

    @Override
    public void write(JsonWriter out, Collection<T> value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginArray();
        for (T element : value) {
            elementTypeAdapter.write(out, element);
        }
        out.endArray();

    }

    @Override
    public Collection<T> read(JsonReader in) throws IOException {
//        val constructorConstructor = ConstructorConstructor(HashMap())
//        val constructor = constructorConstructor.get(typeToken)
//        as ObjectConstructor<out Collection<T>>
//
//        if (jsonReader.peek() == JsonToken.NULL) {
//            jsonReader.nextNull()
////            return emptyList()
////            这样写会报错：field com.myfittinglife.typeadapterdemo.entities.ListBean.language has type java.util.ArrayList, got kotlin.collections.EmptyList，当ListBean为ArrayList而不是List时会报错
////            return mutableListOf()
//            return constructor.construct()
//        }
        ConstructorConstructor constructorConstructor = new ConstructorConstructor(new HashMap());
        ObjectConstructor<? extends Collection<T>> constructor = (ObjectConstructor<? extends Collection<T>>) constructorConstructor.get(typeToken);

        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return constructor.construct();
        }

        Collection<T> collection = constructor.construct();
        in.beginArray();
        while (in.hasNext()) {
            T instance = elementTypeAdapter.read(in);
            collection.add(instance);
        }
        in.endArray();
        return collection;
    }
}
