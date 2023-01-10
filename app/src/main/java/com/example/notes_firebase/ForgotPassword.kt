package com.example.notes_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.hide()

        gobacktologin.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        passwordRecoverBtn.setOnClickListener {
            val mail=forgotpassword.text.toString()
            if(mail.isEmpty()){
                Toast.makeText(this,"Enter your mail first",Toast.LENGTH_SHORT).show()
            } else {
                //
            }
        }
    }
}
