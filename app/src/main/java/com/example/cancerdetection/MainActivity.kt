package com.example.cancerdetection

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cancerdetection.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db=Firebase.firestore

    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_main)
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val db = Firebase.firestore
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val emailfield=findViewById<EditText>(R.id.email_log)
        val passfield=findViewById<EditText>(R.id.password_log)
        val button_log=findViewById<Button>(R.id.button_log)
        val prog=findViewById<ProgressBar>(R.id.progress_login)
        val forgot=findViewById<TextView>(R.id.forgot)
        
        forgot.setOnClickListener{
            val user_mail=emailfield.text.toString()
            auth.sendPasswordResetEmail(user_mail)
                .addOnSuccessListener {
                    Toast.makeText(this, "An Email has been sent to reset your Password", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Couldn't send forgot password Email", Toast.LENGTH_SHORT).show()
                }
        }

        button_log.setOnClickListener{
            val email=emailfield.text.toString()
            val password=passfield.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                prog.visibility=View.VISIBLE
                login(email,password)
            }
            else{
                prog.visibility=View.INVISIBLE
                Toast.makeText(this,"Please enter email and password",Toast.LENGTH_SHORT).show()
            }
        }


        val t1=findViewById<TextView>(R.id.sign)
        t1.setOnClickListener{
            val exp_signup= Intent(this,MainActivity2::class.java)
            startActivity(exp_signup)
        }
    }
    private fun login(email: String,password: String){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                // FETCHING USER NAME FROM DATABASE
                val userid=FirebaseAuth.getInstance().currentUser!!.uid
                val ref=db.collection("user").document(userid)
                ref.get().addOnSuccessListener{
                    if(it!=null){
                        val prog=findViewById<ProgressBar>(R.id.progress_login)
                        val name= it.data?.get("name")?.toString()
                        Toast.makeText(this,"Welcome $name",Toast.LENGTH_SHORT).show()
                        val home_intent=Intent(this,Home_act::class.java)
                        home_intent.putExtra("name",name)
                        startActivity(home_intent)
                        prog.visibility=View.INVISIBLE
                    }
                }
            }
            else{
                val prog=findViewById<ProgressBar>(R.id.progress_login)
                prog.visibility=View.INVISIBLE
                Toast.makeText(this,"Incorrect Credentials",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
