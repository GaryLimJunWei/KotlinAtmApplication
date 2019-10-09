package ui.fragment


import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation

import com.example.garylim.atmapplication.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.fragment_update_email.*
import kotlinx.android.synthetic.main.fragment_update_password.*
import kotlinx.android.synthetic.main.fragment_update_password.edit_text_password_
import kotlinx.android.synthetic.main.fragment_update_password.layoutPassword
import kotlinx.android.synthetic.main.fragment_update_password.progressbar
import utils.toast



class UpdatePasswordFragment : Fragment() {

    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutPassword.visibility = View.VISIBLE
        layoutUpdatePassword.visibility = View.GONE

        authenticate.setOnClickListener {
            val password = edit_text_password_.text.toString().trim()

            if(password.isEmpty())
            {
                edit_text_password_.error = "Password required"
                edit_text_password_.requestFocus()
                return@setOnClickListener
            }
            currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!,password)
                progressbar.visibility = View.VISIBLE
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        progressbar.visibility = View.GONE
                        when
                        {
                            task.isSuccessful -> {
                                layoutPassword.visibility = View.GONE
                                layoutUpdatePassword.visibility = View.VISIBLE
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException ->
                            {
                                edit_text_new_password.error = "Invalid Password"
                                edit_text_new_password.requestFocus()
                            }
                            else -> context?.toast(task.exception?.message!!)
                        }

                    }
            }


        }

        update.setOnClickListener {
            val password = edit_text_new_password.text.toString().trim()

            if(password.isEmpty())
            {
                edit_text_new_password.error = "Password required"
                edit_text_new_password.requestFocus()
                return@setOnClickListener
            }

            if(password != edit_text_new_password_confirm.text.toString().trim())
            {
                edit_text_new_password_confirm.error = "Password did not match"
                edit_text_new_password_confirm.requestFocus()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                progressbar.visibility = View.VISIBLE
                user.updatePassword(password)
                    .addOnCompleteListener { task ->
                       if(task.isSuccessful)
                       {
                           val action = UpdatePasswordFragmentDirections.actionPasswordUpdate()
                           Navigation.findNavController(it).navigate(action)
                           context?.toast("Password updated!")
                       }
                        else
                       {
                           context?.toast(task.exception?.message!!)
                       }

                    }
            }
        }
    }


}
