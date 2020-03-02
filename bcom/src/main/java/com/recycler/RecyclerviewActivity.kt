package com.recycler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.buel.bcom.R

class RecyclerviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview)

        //rv_main.adapter = TitleContentRecyclerviewAdapter()
        //rv_main.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 1)
    }
}
