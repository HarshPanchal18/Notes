package com.example.notes_firebase

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        auth=FirebaseAuth.getInstance()
        val user=auth.currentUser

        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient= GoogleSignIn.getClient(this,gso)

        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        if(user!=null){
            startActivity(Intent(this,HomeActivity::class.java))
            finish()
        }

        gSignup.setOnClickListener { signInGoogle() }

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
                loginProgress.visibility= View.INVISIBLE

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

    private fun signInGoogle() {
        googleSignInClient.signOut() // clear previous sign-in info

        val signInIntent=googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() ) { result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val task= GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if(task.isSuccessful){
            val account: GoogleSignInAccount?=task.result
            if(account!=null){
                updateUI(account)
            }
        } else {
            Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
            if(it.isSuccessful)
                startActivity(Intent(this,HomeActivity::class.java))
            else
                Toast.makeText(this,it.exception?.message,Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener {
            Toast.makeText(this,it.message,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null) {
            Intent(this,HomeActivity::class.java).also {
                it.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }
}
