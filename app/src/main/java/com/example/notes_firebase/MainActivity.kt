package com.example.notes_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        auth=FirebaseAuth.getInstance()
        val user=auth.currentUser

        if(user!=null){
            finish()
            startActivity(Intent(this,HomeActivity::class.java))
        }

        signup.setOnClickListener{
            startActivity(Intent(this,SignupActivity::class.java))
        }

        gotoforgot.setOnClickListener {
            startActivity(Intent(this,ForgotPassword::class.java))
        }

        login.setOnClickListener {
            loginProgress.visibility= View.VISIBLE

            val mail=loginmail.text.toString()
            val password=loginpassword.text.toString()

            if(mail.isEmpty() or password.isEmpty()){
                Toast.makeText(this,"All fields are Required",Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        checkMailVerifiaction()
                    } else {
                        Toast.makeText(this,"Account doesn't exists",Toast.LENGTH_SHORT).show()
                        loginProgress.visibility= View.INVISIBLE
                    }
                }
            }
        }

        loginProgress
    }

    private fun checkMailVerifiaction() {
        val user=auth.currentUser
        if(user?.isEmailVerified==true){
            Toast.makeText(this,"Logged in",Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this,HomeActivity::class.java))
        } else {
            Toast.makeText(this,"Verify your mail first",Toast.LENGTH_SHORT).show()
            loginProgress.visibility= View.INVISIBLE
            auth.signOut()
        }
    }
}
