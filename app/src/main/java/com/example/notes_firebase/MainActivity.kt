package com.example.notes_firebase

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        if(user!=null){
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
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
                val snackbar= Snackbar.make(loginScreen,"All fields are Required",Snackbar.LENGTH_SHORT)
                val snackBarView:View=snackbar.view
                val params=snackBarView.layoutParams as FrameLayout.LayoutParams
                params.gravity=Gravity.TOP
                snackBarView.setBackgroundColor(Color.BLACK)
                snackbar.show()

                vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener {
                    if(it.isSuccessful){
                        checkMailVerifiaction()
                    } else {
                        val snackbar= Snackbar.make(loginScreen,"Invalid credentials",Snackbar.LENGTH_SHORT)
                        val snackBarView:View=snackbar.view
                        val params=snackBarView.layoutParams as FrameLayout.LayoutParams
                        params.gravity=Gravity.TOP
                        snackBarView.setBackgroundColor(Color.BLACK)
                        snackbar.show()

                        vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE))
                        loginProgress.visibility= View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun checkMailVerifiaction() {
        val user=auth.currentUser
        if(user?.isEmailVerified==true){
            //Toast.makeText(this,"Logged in",Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this,HomeActivity::class.java))
        } else {
            val snackbar= Snackbar.make(loginScreen,"Verify your mail first",Snackbar.LENGTH_SHORT)
            val snackBarView:View=snackbar.view
            val params=snackBarView.layoutParams as FrameLayout.LayoutParams
            params.gravity=Gravity.TOP
            snackBarView.setBackgroundColor(Color.BLACK)
            snackbar.show()

            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE))

            loginProgress.visibility= View.INVISIBLE
            auth.signOut()
        }
    }
}
