package com.ubec.ubecapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ubec.ubecapp.data.DBHelper
import com.ubec.ubecapp.data.model.CodeListModel
import com.ubec.ubecapp.data.model.ProjectModel
import com.ubec.ubecapp.databinding.ActivityMainBinding
import com.ubec.ubecapp.network.ApiAdapter
import com.ubec.ubecapp.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.ubec.ubecapp.R
import com.ubec.ubecapp.data.model.MilestoneModel

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_CODE = 100
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val sharedPrefFile = "IdentityData"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission(com.ubec.ubecapp.MainActivity.Companion.PERMISSION_CODE)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(com.ubec.ubecapp.R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                com.ubec.ubecapp.R.id.nav_home,
                com.ubec.ubecapp.R.id.nav_new_report,
                com.ubec.ubecapp.R.id.nav_reports
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        getMilestones()
        getLGAs()
        getContractors()
        getProjects()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(com.ubec.ubecapp.R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(com.ubec.ubecapp.R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Function to check and request permission.
    private fun checkPermission(requestCode: Int):Boolean {

        return if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET), requestCode)
            false
        } else {
            //Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
            true
        }
    }


    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == com.ubec.ubecapp.MainActivity.Companion.PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage, Camera, Location and Internet Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage, Camera, Location and Internet  Permission Denied", Toast.LENGTH_SHORT).show()
                logOut();
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.ubec.ubecapp.R.id.action_settings -> {
                logOut()
                //Toast.makeText(applicationContext, "click on setting", Toast.LENGTH_LONG).show()
                true
            }
            com.ubec.ubecapp.R.id.action_settings_sync -> {
                loadMasterData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logOut(){
        //implementing remember me with shared preferences
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun loadMasterData(){
        loadProjects()
        loadContractors()
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    private fun loadContractors() {
        try {
            val db = DBHelper(this@MainActivity, null)
            var contractor = db.getCodeList("contractor")
            MainScope().launch {
                val response = withContext(Dispatchers.IO) {
                    ApiAdapter.apiClient.getContractors()
                }
                // Check if response was successful.
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    val records = body!!.Records
                    records.forEach { item ->
                        when {
                            contractor.any { it.code != item.Id.toString()} -> {
                                db.addCodeList(CodeListModel(null,item.Id.toString(),item.Name,"contractor"))
                            }
                        }
                    }
                }
            }
        } catch (e: Exception){
            Log.w("APIError: ",e.message.toString())
        }
    }
    private fun loadProjects() {
        try {
            val userId = getUserId()
            val db = DBHelper(this@MainActivity, null)

            db.reinitialiseProject()
            var rows = db.getProjects()
            //if(rows.count() == 0){
                MainScope().launch {
                    val response = withContext(Dispatchers.IO) {
                        ApiAdapter.apiClient.getProjects()
                    }
                    // Check if response was successful.
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()
                        val records = body!!.Records
                        records.forEach { item ->
                            if(item.OwnedBy != null){
                                if(item.OwnedBy == userId) {
                                    //when {
                                        //rows.any { it.projectId != item.ProjectId.toString()} -> {
                                            db.addProject(ProjectModel(null,
                                                item.ProjectId.toString(), item.SerialNo,
                                                item.Workflow,
                                                item.ProjectType, item.Contractor,
                                                item.Description, item.OwnedBy))
                                        //}
                                        //rows.any { it.projectId == item.ProjectId.toString()} -> {
                                        //    db.updateProject(ProjectModel(null,
                                        //        item.ProjectId.toString(), item.SerialNo,
                                        //        item.Workflow,
                                        //        item.ProjectType, item.Contractor,
                                        //        item.Description, item.OwnedBy))
                                        //}
                                   // }
                                }
                            }
                    }
                }
            }
        } catch (e: Exception){
            Log.w("APIError: ",e.message.toString())
        }
    }

    private fun getLGAs() {
        try {
            val db = DBHelper(this@MainActivity, null)
            var lga = db.getCodeList("lga")
            if(lga.count() == 0){
                MainScope().launch {
                    val response = withContext(Dispatchers.IO) {
                        ApiAdapter.apiClient.getLGAs()
                    }
                    // Check if response was successful.
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()
                        val records = body!!.Records
                        records.forEach {
                            db.addCodeList(CodeListModel(null,it.Id.toString(),it.Name,"lga"))
                        }

                    }
                }
            }
        } catch (e: Exception){
            Log.w("APIError: ",e.message.toString())
        }
    }
    private fun getContractors() {
        try {
            val db = DBHelper(this@MainActivity, null)
            var contractor = db.getCodeList("contractor")
            if(contractor.count() == 0){
                MainScope().launch {
                    val response = withContext(Dispatchers.IO) {
                        ApiAdapter.apiClient.getContractors()
                    }
                    // Check if response was successful.
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()
                        val records = body!!.Records
                        records.forEach {
                            db.addCodeList(CodeListModel(null,it.Id.toString(),it.Name,"contractor"))
                        }
                    }
                }
            }
        } catch (e: Exception){
            Log.w("APIError: ",e.message.toString())
        }
    }
    private fun getMilestones() {
        try {
            val db = DBHelper(this@MainActivity, null)
            var milestones = db.getMilestones()
            if(milestones.count() == 0){
                MainScope().launch {
                    val response = withContext(Dispatchers.IO) {
                        ApiAdapter.apiClient.getMilestones()
                    }
                    // Check if response was successful.
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()
                        val records = body!!.Records
                        records.forEach {
                            db.addMilestone(MilestoneModel(null,it.ProjectType.toString(),it.Percentage,it.Description))
                        }

                    }
                }
            }
        } catch (e: Exception){
            Log.w("APIError:",e.message.toString())
        }
    }
    private fun getUserId():String?{
        try {
            //implementing remember me with shared preferences
            val sharedPreferences: SharedPreferences =
                this.getSharedPreferences(sharedPrefFile,
                    Context.MODE_PRIVATE)
            return sharedPreferences.getString("userId", null)
        }catch(ex:Exception){}
        return null
    }
    private fun getProjects() {
        try {
            val userId = getUserId()
            val db = DBHelper(this@MainActivity, null)
            var rows = db.getProjects()
            if(rows.count() == 0){
                MainScope().launch {
                    val response = withContext(Dispatchers.IO) {
                        ApiAdapter.apiClient.getProjects()
                    }
                    // Check if response was successful.
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()
                        val records = body!!.Records
                        records.forEach {
                            if(it.OwnedBy != null){
                            if(it.OwnedBy == userId) {
                                db.addProject(ProjectModel(null,
                                    it.ProjectId.toString(), it.SerialNo,
                                    it.Workflow,
                                    it.ProjectType, it.Contractor,
                                    it.Description, it.OwnedBy))
                            }
                            }
                        }

                    }
               }
            }
        } catch (e: Exception){
            Log.w("APIError: ",e.message.toString())
        }
    }

}


