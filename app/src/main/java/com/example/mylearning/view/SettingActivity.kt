package com.example.mylearning.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivitySettingBinding
import com.example.mylearning.databinding.DialogSetAsDefault1Binding
import kotlin.getValue




class SettingActivity : AppCompatActivity() {
    private val prefs by lazy {
        getSharedPreferences("app_settings", MODE_PRIVATE)
    }

    private var checkSetAsDefault = false
    private var checkAppDefault = false

    private var backToClearDefaultGuideActivity= false

    companion object {
        private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        private const val KEY_NIGHT_MODE = "night_mode"
    }

    private lateinit var binding: ActivitySettingBinding
    @RequiresApi(Build.VERSION_CODES.R)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        window.insetsController?.let {
            it.hide(WindowInsets.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setupItems()
        setupListeners()
        setupToggles()
    }

    override fun onResume() {
        super.onResume()
        val defaultPkg = findCurrentDefaultPptHandler()
        checkAppDefault = defaultPkg == packageName
        binding.itemSetDefaultApp.root.visibility = if(checkAppDefault) View.GONE else View.VISIBLE
        if(backToClearDefaultGuideActivity&&defaultPkg==null){
            val dialog1 = SetAsDefaultBottomDialog1()
            if(supportFragmentManager.findFragmentByTag(SetAsDefaultBottomDialog1.TAG)==null){
                dialog1.show(supportFragmentManager, SetAsDefaultBottomDialog1.TAG)
            }
            backToClearDefaultGuideActivity = false
        }
    }

    override fun onStop() {
        super.onStop()
        if(checkSetAsDefault){
            val intent = Intent(this, ClearDefaultGuideActivity::class.java)
            startActivity(intent)
            checkSetAsDefault = false
            backToClearDefaultGuideActivity = true
        }
    }

//    private val clearDefaultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) {
//        // Callback này sẽ chạy khi bạn quay lại từ ClearDefaultGuideActivity
//        // Kiểm tra lại nếu bây giờ chưa có app mặc định thì hiện Dialog 1
//        val defaultPkg = findCurrentDefaultPptHandler()
//        if (defaultPkg == null) {
//            val dialog1 = SetAsDefaultBottomDialog1()
//            if(supportFragmentManager.findFragmentByTag(SetAsDefaultBottomDialog1.TAG)==null){
//                dialog1.show(supportFragmentManager, SetAsDefaultBottomDialog1.TAG)
//            }
//        }
//    }

    private fun setupItems() {
        binding.itemChangeLanguage.ivIcon.setImageResource(R.drawable.setting_activity_icon1)
        binding.itemChangeLanguage.tvTitle.setText(R.string.change_language)
        binding.itemAddWidget.ivIcon.setImageResource(R.drawable.setting_activity_icon2)
        binding.itemAddWidget.tvTitle.setText(R.string.add_widget)
        binding.itemSetDefaultApp.ivIcon.setImageResource(R.drawable.setting_activity_icon3)
        binding.itemSetDefaultApp.tvTitle.setText(R.string.set_default_app)

        binding.itemFeatureRequest.ivIcon.setImageResource(R.drawable.setting_activity_icon4)
        binding.itemFeatureRequest.tvTitle.setText(R.string.feature_request)
        binding.itemMoreFiles.ivIcon.setImageResource(R.drawable.setting_activity_icon5)
        binding.itemMoreFiles.tvTitle.setText(R.string.browse_more_files)
        binding.itemShare.ivIcon.setImageResource(R.drawable.setting_activity_icon6)
        binding.itemShare.tvTitle.setText(R.string.share_this_app)
        binding.itemSendFeedback.ivIcon.setImageResource(R.drawable.setting_activity_icon7)
        binding.itemSendFeedback.tvTitle.setText(R.string.send_feedback)

        binding.itemKeepScreenOn.ivIcon.setImageResource(R.drawable.setting_activity_icon8)
        binding.itemKeepScreenOn.tvTitle.setText(R.string.keep_screen_on)
        binding.itemKeepScreenOn.ivArrow.setImageResource(R.drawable.setting_activity_iconoff)
        binding.itemNightMode.ivIcon.setImageResource(R.drawable.setting_activity_icon9)
        binding.itemNightMode.tvTitle.setText(R.string.night_mode)
        binding.itemNightMode.ivArrow.setImageResource(R.drawable.setting_activity_iconoff)

        binding.itemPrivacyPolicy.ivIcon.setImageResource(R.drawable.setting_activity_icon10)
        binding.itemPrivacyPolicy.tvTitle.setText(R.string.privacy_policy)
        binding.itemTerms.ivIcon.setImageResource(R.drawable.setting_activity_icon11)
        binding.itemTerms.tvTitle.setText(R.string.terms_and_conditions)
        binding.itemAboutUs.ivIcon.setImageResource(R.drawable.setting_activity_icon12)
        binding.itemAboutUs.tvTitle.setText(R.string.about_us)
//        binding nav
        binding.bottomNavigation.itemHomeNav.ivIcon.setImageResource(R.drawable.setting_activity_home)
        binding.bottomNavigation.itemHomeNav.tvTitle.setText(R.string.home)
        binding.bottomNavigation.itemFavouriteNav.ivIcon.setImageResource(R.drawable.setting_activity_favourite)
        binding.bottomNavigation.itemFavouriteNav.tvTitle.setText(R.string.favorite)
        binding.bottomNavigation.itemToolNav.ivIcon.setImageResource(R.drawable.setting_activity_tool)
        binding.bottomNavigation.itemToolNav.tvTitle.setText(R.string.tools)
        binding.bottomNavigation.itemSettingNav.ivIcon.setImageResource(R.drawable.setting_activity_setting)
        binding.bottomNavigation.itemSettingNav.tvTitle.setText(R.string.settings)
        binding.bottomNavigation.itemSettingNav.tvTitle.setTextColor(Color.parseColor("#FF5C01"))
    }

    private fun setupListeners(){
        binding.itemChangeLanguage.root.setOnClickListener {
            val intent = Intent(this, LanguageActivity2::class.java)
            startActivity(intent)
        }

        binding.bottomNavigation.itemHomeNav.root.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoPremium.setOnClickListener {
            val intent = Intent(this, PremiumActivity::class.java)
            startActivity(intent)
        }

        binding.itemSendFeedback.root.setOnClickListener {
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }

        binding.itemFeatureRequest.root.setOnClickListener {
            val intent = Intent(this, FeatureRequestActivity::class.java)
            startActivity(intent)
        }

        binding.itemAddWidget.root.setOnClickListener {
            if (supportFragmentManager.findFragmentByTag(AddWidgetBottomSheet.TAG) == null) {
                val bottomSheet = AddWidgetBottomSheet()
                bottomSheet.show(supportFragmentManager, AddWidgetBottomSheet.TAG)
            }
        }

        binding.itemSetDefaultApp.root.setOnClickListener {
            handleSetDefaultClick()
        }

        binding.itemShare.root.setOnClickListener {
            shareApp()
        }

    }

    private fun setupToggles(){
        applyKeepScreenOn(prefs.getBoolean(KEY_KEEP_SCREEN_ON,false))
        binding.itemKeepScreenOn.root.setOnClickListener {
            val next = !prefs.getBoolean(KEY_KEEP_SCREEN_ON,false)
            applyKeepScreenOn(next)
            prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, next).apply()
        }
        applyNightMode(prefs.getBoolean(KEY_NIGHT_MODE,false))
        binding.itemNightMode.root.setOnClickListener {
            val next = !prefs.getBoolean(KEY_NIGHT_MODE, false)
            applyNightMode(next)
            prefs.edit().putBoolean(KEY_NIGHT_MODE, next).apply()
        }
    }

    private fun applyKeepScreenOn(enabled: Boolean){
        if(enabled){
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            binding.itemKeepScreenOn.ivArrow.setImageResource(R.drawable.setting_activity_iconon)
        }else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            binding.itemKeepScreenOn.ivArrow.setImageResource(R.drawable.setting_activity_iconoff)
        }
    }
    private fun applyNightMode(enabled: Boolean){
        if(enabled){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.itemNightMode.ivArrow.setImageResource(R.drawable.setting_activity_iconon)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.itemNightMode.ivArrow.setImageResource(R.drawable.setting_activity_iconoff)
        }
        delegate.applyDayNight()
    }

    private fun handleSetDefaultClick() {
        val defaultPkg = findCurrentDefaultPptHandler()
        if(defaultPkg != null){
            Toast.makeText(this, "App mặc định: $defaultPkg", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Không tìm thấy app mặc định khác", Toast.LENGTH_SHORT).show()
        }
        val hasOtherDefault = defaultPkg != null && defaultPkg != packageName
        if(hasOtherDefault){
            val appInfo = packageManager.getApplicationInfo(defaultPkg, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()
            val bottomDialog3 = SetAsDefaultBottomDialog3(onGoToSettings = {checkSetAsDefault = true }).apply { arguments =
                Bundle().apply {
                    putString("arg_pkg", defaultPkg)
                    putString("arg_app_name", appName)
                }
            }
            if(supportFragmentManager.findFragmentByTag(SetAsDefaultBottomDialog3.TAG)==null){
                bottomDialog3.show(supportFragmentManager, SetAsDefaultBottomDialog3.TAG)
            }
        } else {
            val bottomDialog1 = SetAsDefaultBottomDialog1()
            if(supportFragmentManager.findFragmentByTag(SetAsDefaultBottomDialog1.TAG)==null){
                bottomDialog1.show(supportFragmentManager, SetAsDefaultBottomDialog1.TAG)
            }
        }
    }

    /**
     * Tìm app đang giữ mặc định mở PPT/PPTX; trả về packageName hoặc null nếu không có.
     */
    private fun findCurrentDefaultPptHandler(): String? {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse("content://hahaha.pptx"), "application/vnd.openxmlformats-officedocument.presentationml.presentation")
        }
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val defaultPkg = resolveInfo?.activityInfo?.packageName

        if(defaultPkg == null) return null
        if(defaultPkg == "android" || defaultPkg.contains("resolver")) return null
        return defaultPkg
    }

    private fun shareApp(showChooser: Boolean = false) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
            // Thay link bằng link thực tế trên store nếu có
            putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=$packageName")
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        if (showChooser) {
            // Chỉ hiện chooser, không có “Chỉ một lần / Luôn chọn”
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_this_app)))
        } else {
            // Mở trực tiếp để hệ thống hiện sheet “Chỉ một lần / Luôn chọn”
            startActivity(shareIntent)
        }
    }


    //    private fun handleSetDefaultClick() {
//        val defaultPkg = findCurrentDefaultPptHandler()
//        if (defaultPkg != null) {
//            Toast.makeText(this, "App mặc định: $defaultPkg", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Không tìm thấy app mặc định khác", Toast.LENGTH_SHORT).show()
//        }
// //       val hasOtherDefault = defaultPkg != null && defaultPkg != packageName
//        val hasOtherDefault = defaultPkg != null
//
//        if (hasOtherDefault) {
//            val bottomDialog3 = SetAsDefaultBottomDialog3(
//                onGoToSettings = {
//                    checkSetAsDefault = true
//                }
//            ).apply {
//                arguments = Bundle().apply { putString("arg_pkg", defaultPkg) }
//            }
//            if (supportFragmentManager.findFragmentByTag(SetAsDefaultBottomDialog3.TAG) == null) {
//                bottomDialog3.show(
//                    supportFragmentManager,
//                    SetAsDefaultBottomDialog3.TAG
//                )
//            }
//        } else {
//            val bottomDialog1 = SetAsDefaultBottomDialog1()
//            if (supportFragmentManager.findFragmentByTag(SetAsDefaultBottomDialog1.TAG) == null) {
//                bottomDialog1.show(
//                    supportFragmentManager,
//                    SetAsDefaultBottomDialog1.TAG
//                )
//            }
//        }
//    }

//        private fun findCurrentDefaultPptHandler(): String? {
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            val mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
//            setDataAndType(Uri.parse("content://dummy.pptx"), mimeType)
//        }
//
//        // 1. Lấy App mà hệ thống sẽ dùng để mở Intent này
//        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
//        val defaultPkg = resolveInfo?.activityInfo?.packageName
//
//        // 2. Nếu không tìm thấy gì
//        if (defaultPkg == null) return null
//
//        // 3. Nếu nó trả về "android" hoặc "com.google.android.emergency" ...
//        // thì có nghĩa là CHƯA có app nào được set "Always" (Luôn luôn), nó đang hiện bảng chọn.
//        if (defaultPkg == "android" || defaultPkg.contains("resolver")) {
//            return null
//        }
//
//        // 4. Nếu nó trả về chính App của mình
//        if (defaultPkg == packageName) {
//            return null
//        }
//
//        // 5. Nếu đến đây thì chắc chắn defaultPkg là một App cụ thể khác (WPS Office, OfficeSuite, v.v.)
//        return defaultPkg
//    }


//    private fun findCurrentDefaultPptHandler(): String? {
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            val mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
//            // Sử dụng một Uri hợp lệ hoặc chỉ cần type
//            setType(mimeType)
//        }
//
//        // Lấy tất cả các App có thể mở file này
//        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
//        val defaultPkg = resolveInfo?.activityInfo?.packageName
//
//        // Nếu App mặc định KHÔNG PHẢI là App của mình thì mới coi là "có App khác đang chiếm"
//        return if (defaultPkg != null && defaultPkg != packageName && defaultPkg != "android") {
//            defaultPkg
//        } else {
//            null
//        }
//    }




//    private fun findCurrentDefaultPptHandler(): String? {
//        // Thử lần lượt các mime thường gặp
//        val candidates = listOf(
//            "application/vnd.openxmlformats-officedocument.presentationml.presentation" to "dummy.pptx",
//            "application/vnd.ms-powerpoint" to "dummy.ppt"
//        )
//        val schemes = listOf("content", "file")
//        candidates.forEach { (mime, fileName) ->
//            schemes.forEach { scheme ->
//                val intent = Intent(Intent.ACTION_VIEW).apply {
//                    setDataAndType(
//                        Uri.parse("$scheme://com.example.mylearning/$fileName"),
//                        mime
//                    )
//                    addCategory(Intent.CATEGORY_DEFAULT)
//                }
//                val resolveInfo = packageManager.resolveActivity(
//                    intent,
//                    PackageManager.MATCH_DEFAULT_ONLY
//                )
//                if (resolveInfo != null) {
//                    return resolveInfo.activityInfo?.packageName
//                }
//            }
//        }
//        return null
//    }
//    private fun findCurrentDefaultPptHandler(): String? {
//        val intent = Intent(Intent.ACTION_VIEW).apply {
//            setDataAndType(
//                Uri.fromParts("file", "dummy.pdf", null),
//                "application/pdf"
//            )
//            addCategory(Intent.CATEGORY_DEFAULT)
//        }
//
//        val activities = packageManager.queryIntentActivities(
//            intent,
//            PackageManager.MATCH_DEFAULT_ONLY
//        )
//
//        for (ri in activities) {
//            if (ri.preferredOrder > 0) {
//                return ri.activityInfo.packageName
//            }
//        }
//        return null
//    }


//     private fun findCurrentDefaultPptHandler(): String? {
//         val intent = Intent(Intent.ACTION_VIEW).apply {
//             setDataAndType(
//                 Uri.parse("content://com.example.mylearning/dummy.pptx"),
//                 "application/vnd.openxmlformats-officedocument.presentationml.presentation"
//             )
//             addCategory(Intent.CATEGORY_DEFAULT)
//         }
//         val resolveInfo = packageManager.resolveActivity(
//             intent,
//             PackageManager.MATCH_DEFAULT_ONLY
//         ) ?: return null
//         return resolveInfo.activityInfo?.packageName
//     }


}