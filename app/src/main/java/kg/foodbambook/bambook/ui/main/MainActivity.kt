package kg.foodbambook.bambook.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.santalu.maskedittext.BuildConfig
import kg.foodbambook.bambook.R
import kg.foodbambook.bambook.SaveUserToken
import kg.foodbambook.bambook.gone
import kg.foodbambook.bambook.model.Promotion
import kg.foodbambook.bambook.model.version.Version
import kg.foodbambook.bambook.ui.auth.SignInViewModel
import kg.foodbambook.bambook.ui.setupWithNavController
import kg.foodbambook.bambook.ui.splash.SplashScreenActivity
import kotlinx.android.synthetic.main.layout_promotion.view.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {
    companion object {
        lateinit var bottomNavigationView: BottomNavigationView
    }

    private var currentNavController: LiveData<NavController>? = null
    private lateinit var viewModel: SignInViewModel
    private lateinit var promotionViewModel: PromotionViewModel
    override val coroutineContext: CoroutineContext =
        Job() + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        Log.e("TOken", SaveUserToken.getToken(this).toString())
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
        val previouslyStarted = prefs.getBoolean(getString(R.string.pref_previously_started), false)
        if (!previouslyStarted) {
            val edit = prefs.edit()
            edit.putBoolean(getString(R.string.pref_previously_started), java.lang.Boolean.TRUE)
            edit.apply()
            startActivity(Intent(this, SplashScreenActivity::class.java))
            finish()
        }

    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SignInViewModel::class.java)

        viewModel.data.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                checkVersion(it as Version)
            }
        })

        promotionViewModel =
            ViewModelProviders.of(this).get(PromotionViewModel::class.java)
        promotionViewModel.promotion.observe(this, androidx.lifecycle.Observer {
            if (it.isActive) {
                showPromotion(it)
            }
        })
        promotionViewModel.getPromotion(this)
    }

    @SuppressLint("InflateParams")
    private fun showPromotion(promotion: Promotion) = with(promotion) {
        val view = layoutInflater.inflate(R.layout.layout_promotion, null, false)
        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupWindow.animationStyle = R.style.PromotionAnimation

        Glide.with(view).load(image).into(view.iv_promotion)
        view.iv_close.setOnClickListener {
            popupWindow.dismiss()
        }

        launch {
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()

    }

    private fun setupBottomNavigationBar() {
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val profile = if (SaveUserToken.hasToken(this)) R.navigation.profile else R.navigation.auth
        val navGraphIds = listOf(
            R.navigation.main_menu,
            R.navigation.basket,
            profile,
            R.navigation.discount,
            R.navigation.contacts
        )

        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentNavController?.value?.popBackStack() != true) {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAppVersion(this)
    }

    private fun checkVersion(version: Version) {
        if (version.android_version.isNotEmpty() && version.android_version != BuildConfig.VERSION_NAME) {
            showAlert(version)
        }
    }

    private fun showAlert(version: Version) {
        val builder = AlertDialog.Builder(this)

        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_version, findViewById(R.id.dialog_version))
//        boom(view.tv_version_skip, view.tv_version_update)
        val tv_des = view.findViewById<TextView>(R.id.tv_version_description)
        val tv_ver = view.findViewById<TextView>(R.id.tv_version_update)
        val tv_skip = view.findViewById<TextView>(R.id.tv_version_skip)

        builder.setView(view)
        builder.setCancelable(false)

        val dialog = builder.create()

        view.apply {
            tv_des.text =
                resources.getString(R.string.update_description, version.android_version)

            tv_ver.setOnClickListener {
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$packageName")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                        )
                    )
                }
            }

            if (!version.android_force_update) {
                tv_skip.setOnClickListener {
                    dialog.dismiss()
                }
            } else {
                tv_skip.gone()
            }
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}

