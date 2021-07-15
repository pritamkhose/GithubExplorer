package com.pritam.githubexplorer.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.pritam.githubexplorer.R
import com.pritam.githubexplorer.databinding.ActivityMainBinding
import com.pritam.githubexplorer.extensions.addFragment
import com.pritam.githubexplorer.ui.fragment.UserSearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define the listener for binding
        mBinding =  DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(mBinding.toolbar)

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
                 finishAffinity()
                 true
             }
//             android.R.id.home -> {
//                 onBackPressed()
//                 true
//             }
             else -> super.onOptionsItemSelected(item)
         }
     }
}
