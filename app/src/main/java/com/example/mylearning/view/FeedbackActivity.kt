package com.example.mylearning.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.mylearning.R
import com.example.mylearning.databinding.ActivityFeedbackBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale


data class AppLog(
    val appName: String,
    val versionCode: Long,
    val installTime : String,
    val apiLevel: Int,
    val deviceModel: String,
    val locale: String,
    val notificationGranted: Boolean,
    val timeEnterApp: Int,
    val timeShowNotification: Int,
    val timeClickedNotification: Int,
    val timestamp: String
)
class FeedbackActivity : AppCompatActivity() {

    private var firstStartActivity: Boolean = false
    private lateinit var binding: ActivityFeedbackBinding

    private var hasTouched = false

    private val prefs by lazy {
        getSharedPreferences("feedback_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDefaultState()
        setupUi()
        setupListeners()
        getApplog()
    }

    private fun setupUi() {
        binding.fbIssueCannotOpenFile.tvText.setText(R.string.fb_issue_cannot_open_file)
        binding.fbIssueErrorBug.tvText.setText(R.string.fb_issue_error_bug)
        binding.fbIssueSlow.tvText.setText(R.string.fb_issue_slow)
        binding.fbIssueTooManyNotifications.tvText.setText(R.string.fb_issue_too_many_notifications)
        binding.fbIssueSuggestion.tvText.setText(R.string.fb_issue_suggestion)
        binding.fbIssueOther.tvText.setText(R.string.fb_issue_other)
    }

    private val items by lazy {
        listOf(
            binding.fbIssueCannotOpenFile.tvText,
            binding.fbIssueErrorBug.tvText,
            binding.fbIssueSlow.tvText,
            binding.fbIssueTooManyNotifications.tvText,
            binding.fbIssueSuggestion.tvText,
            binding.fbIssueOther.tvText
        )
    }
    private fun initDefaultState() {
        if(firstStartActivity) return

        val editor = prefs.edit()

        items.forEachIndexed { index, tv ->
            editor.putBoolean("issue_$index", false).apply()
            tv.isSelected = false
        }
        firstStartActivity = true
    }

    private fun setupListeners() {
        binding.etDetail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                hasTouched = true
                checkCharBeforeSend()
            }
        }
        binding.etDetail.doAfterTextChanged  {
            if(hasTouched) checkCharBeforeSend()
        }
        items.forEachIndexed { index, tv ->
            tv.setOnClickListener {
                val state = !prefs.getBoolean("issue_$index", false)
                prefs.edit().putBoolean("issue_$index",state).apply()
                tv.isSelected = state
            }
        }
        binding.ivIconBack.setOnClickListener {
            finish()
        }
        binding.tvBtnSend.setOnClickListener {
            if (!checkCharBeforeSend()) return@setOnClickListener
            if(checkFeedbackBeforeSend()) {
                var issueTypes = StringBuilder()
                items.forEachIndexed { index, tv ->
                    if(tv.isSelected) {
                        if(issueTypes.isNotEmpty()) issueTypes.append(", ")
                        issueTypes.append(tv.text.toString() )
                    }
                }
                val description = binding.etDetail.text?.toString().orEmpty()
                val headBody = getString(R.string.fb_head_email)
                val divider: String = "===================="
                val applogString = getApplog().toPrettyString()
                val endBody = "Thông tin để chúng tôi có thể khắc phục sự cố của bạn nhanh hơn:\n" +
                        "======================\n"+"$applogString\n" + "======================\n" + "Bằng cách gửi email này, bạn đồng ý chia sẻ thông tin trên với chúng tôi để giúp cải thiện ứng dụng."
                val subject = getString(R.string.fb_title)
                val body = "$headBody\n$divider\n$issueTypes\n$description\n$endBody"
                val uri = Uri.parse(
                    "mailto:office.adv.support@gmail.com" +
                            "?subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(body)
                )
                val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                }
                try {
                    startActivity(intent)
                }catch (e: Exception){
                    Toast.makeText(this, "Không thể gửi phản hồi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkCharBeforeSend(): Boolean{
        val length = binding.etDetail.text?.length?:0
        val check2 = length >= 6
        binding.tvMinChars.visibility = if (hasTouched&&!check2) View.VISIBLE else View.GONE
        binding.tvBtnSend.isEnabled = check2
        binding.tvBtnSend.alpha = if(check2) 1f else 0.5f
        return check2
    }

    private fun checkFeedbackBeforeSend(): Boolean{
        val check1 = items.indices.any{ index ->
            prefs.getBoolean("issue_$index",false)
        }
        binding.tvMinFeedbacks.visibility = if(check1) View.GONE else View.VISIBLE

        return check1
    }

    private fun getApplog(): AppLog{
        val appName = getString(applicationInfo.labelRes)
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
        val installTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(packageInfo.firstInstallTime))
        val apiLevel = Build.VERSION.SDK_INT
        val deviceModel = Build.MODEL
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0].toString()
        } else {
            resources.configuration.locale.toString()
        }
        val notificationGranted =
            NotificationManagerCompat.from(this).areNotificationsEnabled()
        val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(System.currentTimeMillis()))
        val prefs = getSharedPreferences("app_log", MODE_PRIVATE)
        val timeEnterApp = prefs.getInt("time_enter_app", 0)
        val timeShowNotification = prefs.getInt("time_show_notification", 0)
        val timeClickedNotification = prefs.getInt("time_click_notification", 0)

        return AppLog(
            appName = appName,
            versionCode = versionCode,
            installTime = installTime,
            apiLevel = apiLevel,
            deviceModel = deviceModel,
            locale = locale,
            notificationGranted = notificationGranted,
            timeEnterApp = timeEnterApp,
            timeShowNotification = timeShowNotification,
            timeClickedNotification = timeClickedNotification,
            timestamp = timestamp
        )
    }

    fun AppLog.toPrettyString(): String = """
        appName: $appName
        versionCode: $versionCode
        installTime: $installTime
        apiLevel: $apiLevel
        deviceModel: $deviceModel
        locale: $locale
        notificationGranted: $notificationGranted
        timeEnterApp: $timeEnterApp
        timeShowNotification: $timeShowNotification
        timeClickedNotification: $timeClickedNotification
        timestamp: $timestamp
        """.trimIndent()



}