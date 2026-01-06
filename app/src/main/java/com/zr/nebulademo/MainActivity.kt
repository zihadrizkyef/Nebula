package com.zr.nebulademo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}
