package com.monquiz.ui


import android.content.Intent
import com.monquiz.interfaces.SelectedImageCallBack
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import android.widget.TextView
import android.os.Bundle
import android.view.WindowManager
import com.monquiz.R
import com.monquiz.adapter.TabsAdapter
import com.monquiz.ui.fragments.GalleryFragment
import com.monquiz.ui.fragments.PhotoFragment
import android.widget.Toast
import androidx.annotation.RequiresApi
import android.content.pm.PackageManager
import android.os.Build
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.monquiz.network.InternetStateChecker
import com.monquiz.utils.Constants
import com.monquiz.utils.Permissions
import es.dmoral.toasty.Toasty

class AddProfilePicActivity : BaseActivity(), View.OnClickListener, SelectedImageCallBack {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var tvPostDone: TextView? = null
    private var tvGalleryDone: TextView? = null
    private var comingFrom: String? = ""
    private var postImageUrl: String? = ""
    private var galleryImageUrl: String? = ""
     var listner:SelectedImageCallBack? = null
    private lateinit var internetchecker : InternetStateChecker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.activity_add_profile_pic)
        internetchecker = InternetStateChecker.Builder(this).setCancelable(true).build()
        dataFromIntent
        initializeUi()
        enableActionBar()
        initializePermissions()
        listner = this
    }

    private val dataFromIntent: Unit
        private get() {
            val intent = intent
            if (intent != null) {
                comingFrom = intent.getStringExtra(getString(R.string.intent_coming_from_PlayersJoined))
            }
        }

    private fun enableActionBar() {
        if (supportActionBar != null) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
        }
    }

    private fun initializeUi() {
        val toolbar = findViewById<Toolbar>(R.id.action_toolbar)
        toolbar.title = getString(R.string.add_profile_pic)
        setSupportActionBar(toolbar)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewpager)
        tvGalleryDone = findViewById(R.id.tvGalleryDone)
        tvPostDone = findViewById(R.id.tvPostDone)
    }

    private fun setupViewpager() {
        val mTabsAdapter = TabsAdapter(this, supportFragmentManager,
            tabLayout!!, viewPager!!, tvGalleryDone!!)
        val bundle = Bundle()
        bundle.putString(getString(R.string.from_screen), getString(R.string.add_profile_pic))
        mTabsAdapter.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.gallery)).setTag(resources.getString(R.string.gallery)),
            GalleryFragment::class.java, bundle)
        mTabsAdapter.addTab(tabLayout!!.newTab().setText(resources.getString(R.string.photo)).setTag(resources.getString(R.string.photo)),
            PhotoFragment::class.java, bundle)
    }

    private fun initializePermissions() {
        if (Permissions.checkPermissionForAccessExternalStorage(this)) {
            checkCameraPermission()
        } else {
            Permissions.requestPermissionForAccessExternalStorage(this)
        }
    }

    private fun checkCameraPermission() {
        if (Permissions.checkPermissionForAccessCamera(this)) {
            prepareDetails()
        } else {
            Permissions.requestPermissionForAccessCamera(this)
        }
    }

    private fun prepareDetails() {
        tvGalleryDone!!.setOnClickListener(this)
        tvPostDone!!.setOnClickListener(this)
        setupViewpager()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvGalleryDone -> {
                if (galleryImageUrl!!.isNotEmpty()) {
                    listner!!.selectedImage(galleryImageUrl, true, comingFrom)
                    val intent = Intent()
                    intent.putExtra("imageUrl",galleryImageUrl)
                    intent.putExtra("isFromGallery",true)
                    intent.putExtra("comingFrom",comingFrom)
                    setResult(RESULT_OK,intent)
                    finish()
                } else {
                    Toasty.warning(this, getString(R.string.unable_to_select_image), Toasty.LENGTH_SHORT).show()
                }
            }
            R.id.tvPostDone -> {
                if (postImageUrl!!.isNotEmpty()) {
                    listner!!.selectedImage(postImageUrl, false, comingFrom)
                    finish()
                } else {
                    Toasty.warning(this, getString(R.string.unable_to_select_image),
                        Toasty.LENGTH_SHORT).show()
                }
            }
            else -> {}
        }
    }

    override fun selectedImage(imageUrl: String?, isFromGallery: Boolean, comingFrom: String?) {
        if (isFromGallery) {
            if (imageUrl!!.isNotEmpty()) {
                galleryImageUrl = imageUrl
                tvGalleryDone!!.visibility = View.VISIBLE
                tvPostDone!!.visibility = View.GONE
            } else {
                tvGalleryDone!!.visibility = View.GONE
            }
        } else {
            runOnUiThread {
                if (imageUrl!!.isNotEmpty()) {
                    postImageUrl = imageUrl
                    tvPostDone!!.visibility = View.VISIBLE
                    tvGalleryDone!!.visibility = View.GONE
                } else {
                    tvPostDone!!.visibility = View.GONE
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        if (permissions.isNotEmpty() && grantResults.isNotEmpty()) {
            when (requestCode) {
                Constants.REQUEST_CODE_FOR_EXTERNAL_STORAGE_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializePermissions() // Permission Granted
                } else {
                    checkCameraPermission()
                }
                Constants.REQUEST_CODE_FOR_CAMERA_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkCameraPermission() // Permission Granted
                } else {
                    prepareDetails()
                }
                else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        internetchecker.start()
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
    }

    override fun onDestroy() {
        super.onDestroy()
        internetchecker.stop()
    }

    override fun onStop() {
        super.onStop()
        internetchecker.stop()
    }

    override fun onPause() {
        super.onPause()
        internetchecker.stop()
    }

}