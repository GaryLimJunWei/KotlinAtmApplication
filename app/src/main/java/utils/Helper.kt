package utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import ui.HomeActivity
import ui.MainActivity

fun Context.toast(message:String) =
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()

fun Context.login()
{
        val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //IF YOU don't do this, when user press back button user will see the register again
        }
        startActivity(intent)
}

fun Context.logout()
{
        val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //IF YOU don't do this, when user press back button user will see the register again
        }
        startActivity(intent)
}

