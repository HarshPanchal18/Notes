package com.example.notes_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        signup.setOnClickListener{
            startActivity(Intent(this,SignupActivity::class.java))
        }

        gotoforgot.setOnClickListener {
            startActivity(Intent(this,ForgotPassword::class.java))
        }

        login.setOnClickListener {
            val mail=loginmail.text.toString()
            val password=loginpassword.text.toString()

            if(mail.isEmpty() or password.isEmpty()){
                Toast.makeText(this,"All fields are Required",Toast.LENGTH_SHORT).show()
            } else {
                //
            }
        }
    }
}
