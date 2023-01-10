package com.example.notes_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()

        gotologin.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        signup.setOnClickListener {
            val mail=signupmail.text.toString()
            val password=signuppassword.text.toString()

            if(mail.isEmpty() or password.isEmpty()){
                Toast.makeText(this,"All fields are required",Toast.LENGTH_SHORT).show()
            } else if(password.length<7){
                Toast.makeText(this,"Password should longer than 7 digits",Toast.LENGTH_SHORT).show()
            } else {
                //
            }
        }
    }
}
