package com.zr.nebulademo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thedeanda.lorem.LoremIpsum
import com.zr.nebula.Nebula
import com.zr.nebulademo.databinding.ActivityMainBinding
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

        val lorem = LoremIpsum.getInstance()

        binding.buttonLogD.setOnClickListener {
            Nebula.d(lorem.getWords(Random.nextInt(3, 20)))
        }
        binding.buttonLogI.setOnClickListener {
            Nebula.i(lorem.getWords(Random.nextInt(3, 20)))
        }
        binding.buttonLogW.setOnClickListener {
            Nebula.w(lorem.getWords(Random.nextInt(3, 20)))
        }
        binding.buttonLogE.setOnClickListener {
            Nebula.e(lorem.getWords(Random.nextInt(3, 20)))
        }
        binding.buttonLog2000.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                repeat(2_000) {
                    val intRandom = (1..4).random()
                    when (intRandom) {
                        1 -> Nebula.i(lorem.getWords(5, 50))
                        2 -> Nebula.w(lorem.getWords(5, 50))
                        3 -> Nebula.d(lorem.getWords(5, 50))
                        4 -> Nebula.e(lorem.getWords(5, 50))
                    }
                }
                Nebula.e("Last loremipsum logs")
            }
        }
        binding.buttonLog100.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                repeat(100) {
                    val intRandom = (1..4).random()
                    when (intRandom) {
                        1 -> Nebula.i(lorem.getWords(5, 50))
                        2 -> Nebula.w(lorem.getWords(5, 50))
                        3 -> Nebula.d(lorem.getWords(5, 50))
                        4 -> Nebula.e(lorem.getWords(5, 50))
                    }
                    delay(Random.nextLong(1000))
                }
                Nebula.e("Last loremipsum logs")
            }
        }
    }
}