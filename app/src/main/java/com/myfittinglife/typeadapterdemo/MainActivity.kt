package com.myfittinglife.typeadapterdemo

import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.*
import com.google.gson.internal.ConstructorConstructor
import com.myfittinglife.typeadapterdemo.entities.Graph
import com.myfittinglife.typeadapterdemo.entities.ListBean
import com.myfittinglife.typeadapterdemo.entities.TestBean
import com.myfittinglife.typeadapterdemo.nulllist.MyCollectionTypeAdapterFactory
import com.myfittinglife.typeadapterdemo.nulllist.MyTypeAdapterFactory2
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.StringBuilder
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ceshi"
    }

    lateinit var typeAdapterRetrofit: Retrofit
    lateinit var normalRetrofit: Retrofit
    val normalGson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val typeAdapterGson =
            GsonBuilder().registerTypeAdapterFactory(MyTypeAdapterFactory()).create()
        typeAdapterRetrofit =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create(typeAdapterGson))
                .baseUrl("http://192.168.14.57:4523/mock/1059885/")
                .build()
        normalRetrofit =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.14.57:4523/mock/1059885/")
                .build()
        val typeAdapterApiService = typeAdapterRetrofit.create(ApiService::class.java)
        val normalApiService = normalRetrofit.create(ApiService::class.java)


        /**
         * 测试官方示例（一）
         */
        btn1.setOnClickListener {
            //{"origin":"0.0","points":["1,2"]}
            //通过PointTypeAdapter处理的
            val gson = GsonBuilder().registerTypeAdapterFactory(MyTypeAdapterFactory()).create()
            val graphAdapter = gson.getAdapter(Graph::class.java)

            //正常的
            val gson1 = Gson()
            val graphAdapter1 = gson1.getAdapter(Graph::class.java)


            val json = "{\"origin\":\"0.0\",\"points\":[\"1,2\"]}"
            //PointTypeAdapter处理
            val graph = graphAdapter.fromJson(json)
            Log.i(TAG, "graph: $graph")
            try {
                //未经处理正常解析的
                val graph1 = graphAdapter1.fromJson(json)
                Log.i(TAG, "graph1: $graph1")
            } catch (e: Exception) {
                Log.i(TAG, "解析生成graph1失败：${e.message}")
            }


            val point = Point(1, 2)
            val graph2 = Graph("0.0", listOf(point))
            val json1 = graphAdapter.toJson(graph2)
            Log.i(TAG, "PointTypeAdapter处理生成的: $json1")
            val json2 = graphAdapter1.toJson(graph2)
            Log.i(TAG, "未经PointTypeAdapter处理生成的: $json2")


//            //两种写法
//            val a = gson.fromJson<Graph>(json,Graph::class.java)
//            Log.i(TAG, "onCreate: $a")

        }


        //未使用StringTypeAdapter处理
        //返回的数据为：{"code":200,"testParam":null}
        //结果：TestBean(testParam=null, code=200)
        btn2.setOnClickListener {
            normalApiService.getNullStringData().enqueue(object : Callback<TestBean> {
                override fun onResponse(call: Call<TestBean>, response: Response<TestBean>) {
                    response.body()?.let {
                        tvMsg.text = "code:${it.code}\n testParam:${it.testParam}"
                        Log.i(TAG, "onResponse: $it")

                    }
                }

                override fun onFailure(call: Call<TestBean>, t: Throwable) {
                    Log.i(TAG, "onFailure: ${t.message}")
                    tvMsg.text = "请求失败：${t.message}"
                }

            })
        }

        //使用StringTypeAdapter处理
        //返回的数据为：{"code":200,"testParam":null}
        //结果：TestBean(testParam=, code=200)
        btn3.setOnClickListener {
            typeAdapterApiService.getNullStringData().enqueue(object : Callback<TestBean> {
                override fun onResponse(call: Call<TestBean>, response: Response<TestBean>) {
                    response.body()?.let {
                        tvMsg.text = "code:${it.code}\n testParam:${it.testParam}"
                        Log.i(TAG, "onResponse: $it")
                    }
                }

                override fun onFailure(call: Call<TestBean>, t: Throwable) {
                    Log.i(TAG, "onFailure: ${t.message}")
                    tvMsg.text = "请求失败：${t.message}"
                }

            })
        }

        //使用StringTypeAdapter解析正常数据JSON数据
        //返回的数据为：{"code":200,"testParam":"正常字段"}
        //结果：TestBean(testParam=正常字段, code=200)
        btn4.setOnClickListener {
            typeAdapterApiService.getNormalData().enqueue(object : Callback<TestBean> {
                override fun onResponse(call: Call<TestBean>, response: Response<TestBean>) {
                    response.body()?.let {
                        tvMsg.text = "code:${it.code}\n testParam:${it.testParam}"
                        Log.i(TAG, "onResponse: $it")
                    }
                }

                override fun onFailure(call: Call<TestBean>, t: Throwable) {
                    Log.i(TAG, "onFailure: ${t.message}")
                    tvMsg.text = "请求失败：${t.message}"
                }
            })
        }

        //测试本地数据
        //测试的数据为：{"code":200,"testParam":null}
        btn5.setOnClickListener {
            val str = "{\n" +
                    "  \"code\": 200,\n" +
                    "  \"testParam\": null\n" +
                    "}"
            val testBean1 = normalGson.fromJson<TestBean>(str, TestBean::class.java)
            val testBean2 = typeAdapterGson.fromJson<TestBean>(str, TestBean::class.java)
            //看会将null转为什么
            Log.i(TAG, "onCreate: str1:$testBean1")
            Log.i(TAG, "onCreate: str2:$testBean2")
            val strBuilder = StringBuilder().let {
                it.append("未使用StringTypeAdapter:code:${testBean1.code}\n testParam:${testBean1.testParam}\n")
                it.append("使用StringTypeAdapter:code:${testBean2.code}\n testParam:${testBean2.testParam}")
            }
            tvMsg.text = strBuilder
        }

        //通过registerTypeAdapter()使用TypeAdapter(基本类型)
        //通过registerTypeHierarchyAdapter使用TypeAdapter(对象)
        btn6.setOnClickListener {

            val gson = GsonBuilder()
                .registerTypeAdapter(String::class.java, StringTypeAdapter())
                .registerTypeHierarchyAdapter(Point::class.java, PointTypeAdapter())
                .create()

            val graphAdapter = gson.getAdapter(Graph::class.java)

            val point = Point(1, 2)
            val graph2 = Graph("0.0", listOf(point))


            val json1 = graphAdapter.toJson(graph2)
            Log.i(TAG, "json1: $json1")
            val json = "{\"origin\":\"0.0\",\"points\":[\"1,2\"]}"
            //PointTypeAdapter处理
            val graph = graphAdapter.fromJson(json)
            Log.i(TAG, "graph: $graph")


            val str = "{\n" +
                    "  \"code\": 200,\n" +
                    "  \"testParam\": null\n" +
                    "}"
            val testBean = gson.fromJson<TestBean>(str, TestBean::class.java)
            Log.i(TAG, "testBean: $testBean")

        }

        //集合字段将null转为[]
        //测试数据{"language":null,"name":"李华"}
        //结果：ListBean(name=李华, language=[])
        btn7.setOnClickListener {
            val str = "{\"language\":null,\"name\":\"李华\"}"

            val normalGson = Gson()
            //通过StackOverflow的答复得到思路
            val stackOverflowGson = GsonBuilder()
                .registerTypeAdapterFactory(
                    MyCollectionTypeAdapterFactory(
                        ConstructorConstructor(
                            HashMap()
                        )
                    )
                )
                .create()
            val myGson = GsonBuilder().registerTypeAdapterFactory(MyTypeAdapterFactory()).create()


            val normalBean = normalGson.fromJson<ListBean>(str, ListBean::class.java)
            val stackOverflowBean = stackOverflowGson.fromJson<ListBean>(str, ListBean::class.java)
            val myBean = myGson.fromJson<ListBean>(str, ListBean::class.java)


            Log.i(TAG, "normalBean: $normalBean")
            Log.i(TAG, "stackOverflowBean: $stackOverflowBean")
            Log.i(TAG, "myBean: $myBean")

            //-----------验证Object转String是否正确
            val listBean = ListBean("张帅", listOf())
//            val listBean = ListBean("张帅", setOf())
            val listBeanStr = stackOverflowGson.toJson(listBean)
            val listBeanStr2 = myGson.toJson(listBean)
            Log.i(TAG, "listBeanStr:$listBeanStr")
            Log.i(TAG, "listBeanStr2:$listBeanStr2")


            //Java方式实现测试
            val javaGson = GsonBuilder().registerTypeAdapterFactory(MyTypeAdapterFactory2()).create()
            val javaBean  =javaGson.fromJson<ListBean>(str,ListBean::class.java)
            Log.i(TAG, "onCreate: javaBean：$javaBean")
            val javaStr = javaGson.toJson(listBean)
            Log.i(TAG, "onCreate: javaStr:$javaStr")

        }
    }
}