package com.example.w7

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Hello_activity : AppCompatActivity() {
    lateinit var lsStudents: RecyclerView
    var strList: Array<String> = arrayOf("Nguyen Van tien", "DucAnh", "MinhViet","phan sy tuan","Duong Duc Khoa")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.helloworld)

        lsStudents = findViewById(R.id.lsStudents)
        lsStudents.layoutManager = LinearLayoutManager(this)
        lsStudents.setHasFixedSize(true)

        val data =ArrayList<DataClass>()

        for (i in 0 until strList.size){
            if(i%2==0)
                data.add(DataClass(R.drawable.logo, strList[i].toString()))
            else
                data.add(DataClass(R.drawable.profile, strList[i].toString()))
        }
        val adapter = RecyclerViewAdapter(data)
//        lsStudents.adapter = adapter
//        lsStudents.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, i, l ->
//        })
//        val arr = ArrayAdapter(this, R.layout.template_layout, strList)
        lsStudents.setAdapter(adapter)

    }
}
