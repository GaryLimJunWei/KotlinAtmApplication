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
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity()
{

    private lateinit var mAuth : FirebaseAuth
    lateinit var editEmail : EditText
    lateinit var editPassword : EditText
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        editEmail = findViewById(R.id.editEmail)
        editPassword = findViewById(R.id.editPW)

        doneBtn.setOnClickListener {
            validateRegister()
        }

        loginBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun validateRegister()
    {
        val email = editEmail.text.toString().trim()
        val password = editPassword.text.toString().trim()
        if(email.isEmpty())
        {
            editEmail.error = "Email required"
            editEmail.requestFocus()
            return
        }
        if( !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editEmail.error = "Valid Email required"
            editEmail.requestFocus()
            return
        }
        if(password.isEmpty() || password.length < 6)
        {
            editPassword.error="6 Char Password required"
            editPassword.requestFocus()
            return
        }

        registerUser(email, password)

    }

    private fun registerUser(email:String,password:String)
    {
        progressBar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email,password)
            //If the task is completed, it will call onCompleteListener
            .addOnCompleteListener(this){ Task ->
                progressBar.visibility = View.GONE
                if(Task.isSuccessful)
                {

                    login()
                    toast("Register Successfully!")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    Task.exception?.message?.let {
                        toast(it)
                    }

                }
            }
    }

    override fun onStart()
    {
        super.onStart()
        //Means the user is already signed in
        mAuth.currentUser?.let {
            login()
        }
    }
}
