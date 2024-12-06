package com.zr.nebulademo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.thedeanda.lorem.LoremIpsum
import com.zr.nebula.Nebula
import com.zr.nebulademo.ui.theme.NebulaDemoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NebulaDemoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val loremIpsum = LoremIpsum.getInstance()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(
            content = {
                Text(text = "Add log i")
            },
            onClick = {
                Nebula.i(loremIpsum.getWords(5, 50))
            }
        )
        Button(
            content = {
                Text(text = "Add log w")
            },
            onClick = {
                Nebula.w(loremIpsum.getWords(5, 50))
            }
        )
        Button(
            content = {
                Text(text = "Add log d")
            },
            onClick = {
                Nebula.d(loremIpsum.getWords(5, 50))
            }
        )
        Button(
            content = {
                Text(text = "Add log e")
            },
            onClick = {
                Nebula.e(loremIpsum.getWords(5, 50))
            }
        )
        Button(
            content = {
                Text(text = "add log 2.000 data")
            },
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    repeat(2_000) {
                        val intRandom = (1..4).random()
                        when (intRandom) {
                            1 -> Nebula.i(loremIpsum.getWords(5, 50))
                            2 -> Nebula.w(loremIpsum.getWords(5, 50))
                            3 -> Nebula.d(loremIpsum.getWords(5, 50))
                            4 -> Nebula.e(loremIpsum.getWords(5, 50))
                        }
                    }
                    Nebula.e("Zihad Rizky")
                }
            }
        )
        Button(
            content = {
                Text(text = "gradually add 100 log")
            },
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    repeat(100) {
                        delay(Random.nextLong(500))
                        val intRandom = (1..4).random()
                        when (intRandom) {
                            1 -> Nebula.i(loremIpsum.getWords(5, 50))
                            2 -> Nebula.w(loremIpsum.getWords(5, 50))
                            3 -> Nebula.d(loremIpsum.getWords(5, 50))
                            4 -> Nebula.e(loremIpsum.getWords(5, 50))
                        }
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NebulaDemoTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainScreen()
        }
    }
}