package ui


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.garylim.atmapplication.R
import utils.logout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this,
            R.id.fragment
        )
        NavigationUI.setupWithNavController(nav_view,navController)
        NavigationUI.setupActionBarWithNavController(this,navController,drawer_layout)
    }

    override fun onSupportNavigateUp() : Boolean
    {
        //This method is to control the hamburger menu on this page
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.fragment),drawer_layout)
    }



    //This function is to set the option menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //Because the logout button is at the option menu toolbar
        // To add onClickListener we need to override this method
        if(item?.itemId == R.id.action_logout)
        {
            AlertDialog.Builder(this).apply {
                setTitle("Are you sure you want to logout?")
                //What are the 2 parameters?
                setPositiveButton("Yes"){_,_ ->
                    FirebaseAuth.getInstance().signOut()
                    logout()
                }
                setNegativeButton("Cancel"){_,_ ->

                }
            } .create().show()
        }
        return super.onOptionsItemSelected(item)
    }
}


