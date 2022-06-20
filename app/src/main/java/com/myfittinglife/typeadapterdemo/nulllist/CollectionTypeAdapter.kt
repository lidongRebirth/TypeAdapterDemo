package com.myfittinglife.typeadapterdemo.nulllist

import com.google.gson.TypeAdapter
import com.google.gson.internal.ConstructorConstructor
import com.google.gson.internal.ObjectConstructor
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * @Author LD
 * @Time 2022/6/15 10:51
 * @Describe 测试
 * @Modify
 */
class CollectionTypeAdapter<T, E>(
    //为什么Java可以直接写typeToken: TypeToken<T>，kotlin不行？
    private val elementTypeAdapter: TypeAdapter<T>, private val typeToken: TypeToken<E>
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
        val constructorConstructor = ConstructorConstructor(HashMap())
        val constructor = constructorConstructor.get(typeToken)
                as ObjectConstructor<out Collection<T>>

        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
//            return emptyList()
//            这样写会报错：field com.myfittinglife.typeadapterdemo.entities.ListBean.language has type java.util.ArrayList, got kotlin.collections.EmptyList，当ListBean为ArrayList而不是List时会报错
//            return mutableListOf()
            return constructor.construct()
        }
        val collection: MutableCollection<T> = constructor.construct() as MutableCollection<T>

        jsonReader.beginArray()
        while (jsonReader.hasNext()) {
            val instance: T = elementTypeAdapter.read(jsonReader)
            collection.add(instance)
        }
        jsonReader.endArray()
        return collection
    }
}
