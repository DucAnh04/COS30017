package com.example.w8_tutorials

import java.io.File

fun main(){
    val list = mutableListOf<Word>()
        File("E:/Data Science/COS30017/w8_tutorials/app/src/main/java/com/example/w8_tutorials/input.txt")
        .forEachLine {
            val temp = it.split(",")
            list.add(Word(temp[0], temp[1].toInt()))
        }

    list.forEach(){
        println("${it.word} -- ${it.num}")
    }
}
data class Word(val word: String, val num: Int)