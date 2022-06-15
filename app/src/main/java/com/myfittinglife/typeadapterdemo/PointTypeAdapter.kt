package com.myfittinglife.typeadapterdemo

import android.graphics.Point
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

/**
 * @Author LD
 * @Time 2022/6/9 14:43
 * @Describe 官方PointTypeAdapter
 * @Modify
 */
class PointTypeAdapter : TypeAdapter<Point>() {
    //Object转为String使用
    //为value写入一段JSON数据
    override fun write(out: JsonWriter, value: Point) {
        if (value == null) {
            out.nullValue()
            return
        }
        var xy = "${value.x},${value.y}"
        out.value(xy)
    }
    //网络请求转Object使用
    //读取一个JSON值将其转为Object对象
    override fun read(reader: JsonReader): Point? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        var xy = reader.nextString()
        var parts = xy.split(",")
        var x = Integer.parseInt(parts[0])
        var y = Integer.parseInt(parts[1])
        return Point(x, y)
    }
}