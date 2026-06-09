package com.zr.nebulademo

import android.Manifest
import android.graphics.Color
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.zr.nebula.Nebula
import com.zr.nebulademo.databinding.ActivityMainBinding
import com.zr.nebulademo.DummyMessageGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        applyWindowInsets()

        binding.buttonLogD.setOnClickListener {
            Nebula.d(DummyMessageGenerator.debugMessages.random())
        }
        binding.buttonLogI.setOnClickListener {
            Nebula.i(DummyMessageGenerator.infoMessages.random())
        }
        binding.buttonLogW.setOnClickListener {
            Nebula.w(DummyMessageGenerator.warningMessages.random())
        }
        binding.buttonLogE.setOnClickListener {
            Nebula.e(DummyMessageGenerator.errorMessages.random())
        }
        binding.buttonLog2000.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                repeat(2_000) {
                    val intRandom = (1..4).random()
                    when (intRandom) {
                        1 -> Nebula.i(DummyMessageGenerator.infoMessages.random())
                        2 -> Nebula.w(DummyMessageGenerator.warningMessages.random())
                        3 -> Nebula.d(DummyMessageGenerator.debugMessages.random())
                        4 -> Nebula.e(DummyMessageGenerator.errorMessages.random())
                    }
                }
            }
        }
        binding.buttonLog100.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                repeat(100) {
                    val intRandom = (1..4).random()
                    when (intRandom) {
                        1 -> Nebula.i(DummyMessageGenerator.infoMessages.random())
                        2 -> Nebula.w(DummyMessageGenerator.warningMessages.random())
                        3 -> Nebula.d(DummyMessageGenerator.debugMessages.random())
                        4 -> Nebula.e(DummyMessageGenerator.errorMessages.random())
                    }
                    delay(Random.nextLong(1000))
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }
    }

    private fun applyWindowInsets() {
        val toolbarPadding = (8 * resources.displayMetrics.density).toInt()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.topAppBar.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
                bottom = toolbarPadding,
            )
            binding.contentContainer.updatePadding(
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }
}
