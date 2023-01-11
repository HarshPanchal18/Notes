package com.example.notes_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()
        auth= FirebaseAuth.getInstance()

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
                auth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(applicationContext,"Registration Successfully",Toast.LENGTH_SHORT).show()
                        sendMailVerification()
                    } else {
                        Toast.makeText(applicationContext,"Failed to register",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun sendMailVerification() {
        val user=auth.currentUser
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener {
                Toast.makeText(applicationContext,"Verification mail is sent, verify and login again",Toast.LENGTH_SHORT).show()
                auth.signOut()
                finish()
                startActivity(Intent(this,MainActivity::class.java))
            }
        } else {
            Toast.makeText(applicationContext,"Failed to sent mail",Toast.LENGTH_SHORT).show()
        }
    }
}
