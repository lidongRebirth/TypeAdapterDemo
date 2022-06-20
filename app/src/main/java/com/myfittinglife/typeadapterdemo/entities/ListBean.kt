package com.myfittinglife.typeadapterdemo.entities

/**
 * @Author LD
 * @Time 2022/6/14 14:25
 * @Describe 测试List集合返回null的处理，两个都可以测试下，都可以转换
 * @Modify
 */
data class ListBean(val name: String, val language: List<String>)
//data class ListBean(val name: String, val language: Set<String>)
//data class ListBean(val name: String, val language: ArrayList<String>)




