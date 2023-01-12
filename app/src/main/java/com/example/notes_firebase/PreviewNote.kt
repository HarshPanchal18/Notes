package com.example.notes_firebase

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_preview_note.*

class PreviewNote : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_note)

        val data=intent

        val colorDrawable = ColorDrawable(Color.parseColor("#FF5693FD"))
        supportActionBar?.setBackgroundDrawable(colorDrawable)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title=data.getStringExtra("title")

        editNoteFAB.setOnClickListener{
            val intent= Intent(this,EditNote::class.java)
            intent.putExtra("title",data.getStringExtra("title"))
            intent.putExtra("description",data.getStringExtra("description"))
            intent.putExtra("docId",data.getStringExtra("docId"))
            startActivity(intent)
        }

        //viewNoteTitle.text=data.getStringExtra("title")
        viewNoteDescription.text=data.getStringExtra("description")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
