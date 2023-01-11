package com.example.notes_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.hide()
        auth= FirebaseAuth.getInstance()

        gobacktologin.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }

        passwordRecoverBtn.setOnClickListener {
            val mail=forgotpassword.text.toString()
            if(mail.isEmpty()){
                Toast.makeText(this,"Enter your mail first",Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(mail).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this,"Password reset mail is sent to your mail",Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(Intent(this,MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Mail is not registered yet", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
