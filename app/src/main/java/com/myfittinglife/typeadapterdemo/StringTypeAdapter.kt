package com.myfittinglife.typeadapterdemo

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * @Author LD
 * @Time 2022/6/9 14:37
 * @Describe 将为null的String直接更改为""
 * @Modify
 */
class StringTypeAdapter : TypeAdapter<String>() {

    //Object转Json数据
    override fun write(out: JsonWriter, value: String?) {

        if (value == null) {
            out.value("")
            //一定要return,不然报错 https://github.com/google/gson/issues/1713
            return
        }
        out.value(value)

//        if(value==null){
//            out.nullValue()
//            return
//        }
//        out.value(value)
    }

    override fun read(jsonReader: JsonReader): String {

        //peek()返回下一个令牌的类型，并不使用它
        if (jsonReader.peek() == JsonToken.NULL) {
            //使用流中的下一个令牌
            //nextNull()如果下一个字段不是null或者流关闭，就会报错
            jsonReader.nextNull()
            return ""
        }
        return jsonReader.nextString()
    }
}