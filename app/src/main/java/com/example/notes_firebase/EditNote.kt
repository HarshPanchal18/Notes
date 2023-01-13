package com.example.notes_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlinx.android.synthetic.main.activity_edit_note.*

class EditNote : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var user:FirebaseUser
    private lateinit var firestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        setSupportActionBar(editNoteToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth=FirebaseAuth.getInstance()
        user= auth.currentUser!!
        firestore=FirebaseFirestore.getInstance()

        val data=intent
        editNoteTitle.setText(data.getStringExtra("title"))
        editNoteDescription.setText(data.getStringExtra("description"))

        saveEditedNoteFAB.setOnClickListener {
            val updatedTitle=editNoteTitle.text.toString()
            val updatedDescription=editNoteDescription.text.toString()

            if(updatedTitle.isEmpty() or updatedDescription.isEmpty()){
                Toast.makeText(this,"Fill the boxes properly",Toast.LENGTH_SHORT).show()
            } else {
                val docRef=firestore.collection("notes")
                    .document(user.uid)
                    .collection("myNotes")
                    .document(data.getStringExtra("noteId").toString())

                val note=HashMap<String,Any>()
                note["title"] = updatedTitle
                note["description"] = updatedDescription
                docRef.set(note).addOnSuccessListener {
                    Toast.makeText(this,"Note updated successfully",Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this,HomeActivity::class.java))
                }.addOnFailureListener {
                    Toast.makeText(this,"Note is failed to update",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
