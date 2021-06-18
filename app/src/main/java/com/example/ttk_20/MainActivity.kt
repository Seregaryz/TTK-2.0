package com.example.ttk_20

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ttk_20.databinding.LayoutContainerBinding
import com.example.ttk_20.ui.keyless_access.KeylessAccessFragment
import com.example.ttk_20.utils.configureSystemBars
import com.example.ttk_20.utils.newRootScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: LayoutContainerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.configureSystemBars()
        }
        binding = LayoutContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.newRootScreen(KeylessAccessFragment::class.java)
    }
}