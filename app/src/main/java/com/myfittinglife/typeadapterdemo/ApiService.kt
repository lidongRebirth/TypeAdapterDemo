package com.myfittinglife.typeadapterdemo

import com.myfittinglife.typeadapterdemo.entities.TestBean
import retrofit2.Call
import retrofit2.http.GET

/**
 * @Author LD
 * @Time 2022/6/9 14:54
 * @Describe 接口
 * @Modify
 */
interface ApiService {

    //获取的数据
    @GET("normalData")
    fun getNormalData(): Call<TestBean>

    //获取String为null的数据
    /*
    {
        "code": 200,
        "testParam": null
    }
     */
    @GET("nullString")
    fun getNullStringData(): Call<TestBean>

}