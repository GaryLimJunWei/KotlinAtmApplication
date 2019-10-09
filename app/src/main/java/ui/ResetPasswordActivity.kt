package ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.garylim.atmapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.button_reset_password
import kotlinx.android.synthetic.main.activity_reset_password.*
import utils.toast
import kotlinx.android.synthetic.main.activity_main.progressbar as progressbar1

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        button_reset_password.setOnClickListener {
            val email = emailValidate.text.toString().trim()
            if(email.isEmpty())
            {
                loginEmail.error = "Email required"
                loginEmail.requestFocus()
                return@setOnClickListener
            }
            if( !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                loginEmail.error = "Valid Email required"
                loginEmail.requestFocus()
                return@setOnClickListener
            }

            progressbar.visibility = View.VISIBLE

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    progressbar.visibility = View.GONE
                    if(task.isSuccessful)
                    {
                        this.toast("Check your email")
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        this.toast(task.exception?.message!!)
                    }
                }

        }
    }
}
