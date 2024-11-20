package com.example.cancerdetection

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cancerdetection.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var db=Firebase.firestore
    override fun setContentView(layoutResID: Int) {
        super.setContentView(R.layout.activity_main)
    }
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

        button_log.setOnClickListener{
            val email=emailfield.text.toString()
            val password=passfield.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()){
                login(email,password)
            }
            else{
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
                // FETCHING USER NAME
                val userid=FirebaseAuth.getInstance().currentUser!!.uid
                val ref=db.collection("user").document(userid)
                ref.get().addOnSuccessListener{
                    if(it!=null){
                        val name= it.data?.get("name")?.toString()
                        Toast.makeText(this,"Welcome $name",Toast.LENGTH_SHORT).show()
                        val home_intent=Intent(this,Home_act::class.java)
                        startActivity(home_intent)
                    }
                }

            }
            else{
                Toast.makeText(this,"Incorrect Credentials",Toast.LENGTH_SHORT).show()
            }
        }
    }
}