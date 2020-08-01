package com.pritam.githubexplorer.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.extensions.addFragment
import com.pritam.githubexplorer.ui.fragment.UserSearchFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val userSerachFragment = UserSearchFragment()
        addFragment(userSerachFragment, R.id.fragment_container)

    }

     override fun onCreateOptionsMenu(menu: Menu): Boolean {
         // Inflate the menu; this adds items to the action bar if it is present.
         menuInflater.inflate(R.menu.menu_main, menu)
         return true
     }

     override fun onOptionsItemSelected(item: MenuItem): Boolean {
         // Handle action bar item clicks here. The action bar will
         // automatically handle clicks on the Home/Up button, so long
         // as you specify a parent activity in AndroidManifest.xml.
         return when (item.itemId) {
             R.id.action_exit -> {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                     finishAffinity()
                 } else {
                     val exitMain = Intent(Intent.ACTION_MAIN)
                     exitMain.addCategory(Intent.CATEGORY_HOME)
                     exitMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                     startActivity(exitMain)
                     finish()
                 }
                 true
             }
             else -> super.onOptionsItemSelected(item)
         }
     }
}
