package com.myfittinglife.typeadapterdemo.nulllist

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * @Author LD
 * @Time 2022/6/15 10:51
 * @Describe 测试
 * @Modify
 */
class CollectionTypeAdapter<T>(
    private val elementTypeAdapter: TypeAdapter<T>
) : TypeAdapter<Collection<T>>() {


    override fun write(out: JsonWriter, value: Collection<T>?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.beginArray()
        for (element in value) {
            elementTypeAdapter.write(out, element)
        }
        out.endArray()
    }

    override fun read(jsonReader: JsonReader): Collection<T> {


        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
//            return emptyList()
//            这样写会报错：field com.myfittinglife.typeadapterdemo.entities.ListBean.language has type java.util.ArrayList, got kotlin.collections.EmptyList，当ListBean为ArrayList而不是List时会报错
            return mutableListOf()
        }
        val list = mutableListOf<T>()
        jsonReader.beginArray()
        while (jsonReader.hasNext()) {
            val instance: T = elementTypeAdapter.read(jsonReader)
            list.add(instance)
        }
        jsonReader.endArray()
        return list
    }
}
