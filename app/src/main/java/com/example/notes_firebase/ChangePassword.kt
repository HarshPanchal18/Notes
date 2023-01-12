package com.example.notes_firebase

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val colorDrawable = ColorDrawable(Color.parseColor("#FF5693FD"))
        supportActionBar?.title="Change Password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setBackgroundDrawable(colorDrawable)
        auth= FirebaseAuth.getInstance()
        user=auth.currentUser!!

        changeBtn.setOnClickListener {
            val password=editPass.text.toString()
            if(checkPasswordFields()){
                user.updatePassword(password).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this,"Password updated successfully",Toast.LENGTH_SHORT).show()
                        auth.signOut()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun checkPasswordFields():Boolean{
        if(editPass.text.toString()== ""){
            tinput_pass.error="Required field"
            tinput_pass.errorIconDrawable=null
            return false
        }

        if(editPass.length() < 8){
            tinput_pass.error="Password should at least 8 digits long"
            tinput_pass.errorIconDrawable=null
            return false
        }

        if(editPass.text.toString()!=editcPass.text.toString()){
            tinput_pass.error="Password must be the same"
            return false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
