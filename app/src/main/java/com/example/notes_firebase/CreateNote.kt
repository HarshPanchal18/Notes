package com.example.notes_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_note.*
import kotlin.collections.HashMap

class CreateNote : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var user:FirebaseUser
    private lateinit var firestore:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        setSupportActionBar(newNoteToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        user= auth.currentUser!!

        saveNoteFAB.setOnClickListener {
            val title=createNoteTitle.text.toString()
            val desc=createNoteDescription.text.toString()

            if(title.isEmpty() || desc.isEmpty()){
                Toast.makeText(applicationContext,"Fields are required",Toast.LENGTH_SHORT).show()
            } else {
                //create note collection for current user's uid
                createProgress.visibility= View.VISIBLE
                val documentRef = firestore.collection("notes")
                    .document(user.uid)
                    .collection("myNotes")
                    .document()

                val note = HashMap<String, Any>()
                note["title"] = title
                note["description"] = desc

                documentRef.set(note).addOnSuccessListener {
                    Toast.makeText(applicationContext,"Note is created",Toast.LENGTH_SHORT).show()
                    createProgress.visibility= View.INVISIBLE
                    startActivity(Intent(this,HomeActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(applicationContext,"Failed to create note",Toast.LENGTH_SHORT).show()
                    createProgress.visibility= View.INVISIBLE
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    class firebaseModel {
        var title: String? = null
        var description: String? = null
    }
}
