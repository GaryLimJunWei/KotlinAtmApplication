package ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.garylim.atmapplication.R
import utils.login
import utils.toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


/*
    The lateinit keyword stands for late initialization. Lateinit comes very handy
    when a non-null initializer cannot be supplied in the constructor,
    but the developer is certain that the variable will not be null when accessing it,
    thus avoiding null checks when referencing it later.


    Using the lateinit keyword has the following requirements:

    1.has to be a var property, val not allowed;
    2.can be either a property inside the body of a class, or a top-level property (Since 1.2);
    3.can only be of non-null type;
    4.primitive types disallowed.

 */

    private lateinit var mAuth : FirebaseAuth
    lateinit var loginEmail : EditText
    lateinit var loginPW : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        loginEmail = findViewById(R.id.loginEmail)
        loginPW = findViewById(R.id.loginPW)



        button_reset_password.setOnClickListener {
                checklogin()

        }

        registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        text_view_forget_password.setOnClickListener {
            val intent = Intent(this,ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checklogin()
    {

        val email = loginEmail.text.toString().trim()
        val password = loginPW.text.toString().trim()
        if(email.isEmpty())
        {

            loginEmail.error = "Email required"
            //requstFocus is for automatic directing to the EditText
            loginEmail.requestFocus()
            return
        }
        // This is to check if the email matches the format of an email
        if( !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            loginEmail.error = "Valid Email required"
            loginEmail.requestFocus()
            return
        }
        if(password.isEmpty() || password.length < 6)
        {
            loginPW.error="6 Char Password required"
            loginPW.requestFocus()
            return
        }

        loginUser(email,password)


    }

    private fun loginUser(email:String,password:String)
    {
        progressbar.visibility = View.VISIBLE
        mAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){ Task ->

                if(Task.isSuccessful)
                {

                    login()
                    //Function is stored at Helper.kt File
                }
                else
                {
                    Task.exception?.message?.let {
                        toast(it)
                    }

                }

                progressbar.visibility = View.GONE
            }
    }

    override fun onStart()
    {
        super.onStart()
        //This method is overriding the onStart method
        // Therefore if the user is already logged in, it will skip the login page
        mAuth.currentUser?.let {
            login()
        }
    }












}
