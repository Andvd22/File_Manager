package com.example.mylearning.view

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.mylearning.R
import com.example.mylearning.adapter.FileAdapter
import com.example.mylearning.databinding.ActivityMainBinding
import com.example.mylearning.databinding.ActivitySecondBinding
import com.example.mylearning.model.FileModel
import com.example.mylearning.model.FileType
import com.example.mylearning.util.PermissionHelper
import com.example.mylearning.viewmodel.FileViewModel
import kotlin.getValue

class SecondActivity : BaseActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var permissionHelper: PermissionHelper
    override val viewModel: FileViewModel by viewModels()

    override val recyclerView: RecyclerView get() = binding.recyclerView
    override val emptyState: View get() = binding.emptyStateLayout
    override val loadingState: View get() = binding.loadingLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)

        permissionHelper = PermissionHelper(this)
        setupListeners()
        setupBaseUI()
    }

    private fun setupListeners(){
        binding.etSearch.addTextChangedListener { text ->
            viewModel.updateFilterParams(query= text?.toString().orEmpty())
//            viewModel.updateQuery(text?.toString().orEmpty())
        }
        binding.goToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}