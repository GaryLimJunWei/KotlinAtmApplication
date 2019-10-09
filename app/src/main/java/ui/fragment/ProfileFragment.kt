package ui.fragment


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.garylim.atmapplication.R
import utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.util.*

class ProfileFragment : Fragment() {

    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"
    private lateinit var imageUri : Uri
    private val REQUEST_IMAGE_CAPTURE = 100

    private val currentUser = FirebaseAuth.getInstance().currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?)
        {
        super.onViewCreated(view, savedInstanceState)
        currentUser?.let { user->
            //To load the picture with the help of the uri we need to import a library
            // Add library MavenCentral inside your buildgradle file
            Glide.with(this)
                .load(user.photoUrl) //Getting the photo from the URL
                .into(image_view)  //Setting it to the image view

            edit_text_name.setText(user.displayName)
            editEmail.text = user.email

            text_phone.text = if(user.phoneNumber.isNullOrEmpty()) "Add number" else user.phoneNumber

            if(user.isEmailVerified)
            {
                text_not_verified.visibility = View.INVISIBLE
            }
            else
            {
                text_not_verified.visibility = View.VISIBLE
            }
        }

        //When the user click the picture it will trigger the method takePictureIntent()
        image_view.setOnClickListener {
            takePictureIntent()
        }

        button_save.setOnClickListener {
            val photo = when{
                ::imageUri.isInitialized -> imageUri
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser.photoUrl
            }

            val name = edit_text_name.text.toString().trim()

            if(name.isEmpty())
            {
                edit_text_name.error = "name required"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }

            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()

            progressbar.visibility = View.VISIBLE

            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful)
                    {
                        progressbar.visibility = View.INVISIBLE
                        context?.toast("Profile Updated")
                    }
                    else
                    {
                        context?.toast(task.exception?.message!!)
                    }
                }

        }

        text_not_verified.setOnClickListener {
            currentUser?.sendEmailVerification()
                ?.addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        context?.toast("Verification Email sent")
                    }
                    else
                    {
                        context?.toast(it.exception?.message!!)
                    }
                }
        }

        text_phone.setOnClickListener {
            val action = ProfileFragmentDirections.actionVerifyPhone()
            Navigation.findNavController(it).navigate(action)
        }

        editEmail.setOnClickListener {
            val action = ProfileFragmentDirections.actionUpdateEmail()
            Navigation.findNavController(it).navigate(action)
        }

        text_password.setOnClickListener {
            val action = ProfileFragmentDirections.actionUpdatePassword()
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun takePictureIntent()
    {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            pictureIntent ->
            pictureIntent.resolveActivity(activity?.packageManager!!)?.also {
                startActivityForResult(pictureIntent,REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
            //IF REQUEST CODE IS EQUAL TO THE REQUEST_IMAGE_CAPTURE
            // AND ALSO TO ENSURE THAT THE PICTURE IS CAPTURE
            // THEN WE WILL CAPTURE THE IMAGE AND PUT IT INSIDE THE INTENT
        {
            val imageBitmap = data?.extras?.get("data") as Bitmap

            uploadImageAndSaveUri(imageBitmap)
        }

    }

    private fun uploadImageAndSaveUri(bitmap: Bitmap)
    {
        val baos = ByteArrayOutputStream()
        val storageRef = FirebaseStorage.getInstance().reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)

        progressbar_pic.visibility = View.VISIBLE
        upload.addOnCompleteListener { uploadTask ->
            progressbar_pic.visibility = View.INVISIBLE
            if (uploadTask.isSuccessful)
            {
                storageRef.downloadUrl.addOnCompleteListener { urlTask ->
                    //Using the let operator and only when the value is not NULL then
                    // the statement will be executed
                    urlTask.result?.let {
                        imageUri = it
                        activity?.toast(imageUri.toString())

                        image_view.setImageBitmap(bitmap)
                    }
                }
            }
            else
            {
                uploadTask.exception?.let {
                    activity?.toast(it.message!!)
                }
            }
        }
    }

}
