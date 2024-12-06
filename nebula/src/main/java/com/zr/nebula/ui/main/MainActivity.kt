package com.zr.nebula.ui.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.zr.nebula.item.Level
import com.zr.nebula.item.Log
import com.zr.nebula.ui.theme.NebulaTheme
import com.zr.nebula.R
import com.zr.nebula.extension.toCurrency
import java.io.File
import java.io.FileWriter

class MainActivity : ComponentActivity() {
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NebulaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val logs = viewModel.logs.collectAsState()
                    LogListScreen(
                        logs = logs,
                        onDelete = { viewModel.deleteAll() },
                        onShare = {
                            val text = logs.value.joinToString("\n\n")
                            val file = writeToFile(this, text)
                            startIntentShareFile(this, file, "com.zr.nebula.fileprovider")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LogListScreen(
    logs: State<List<Log>>,
    onDelete: () -> Unit = {},
    onShare: () -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(start = 16.dp, end = 16.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.lib_name),
                modifier = Modifier.weight(1F),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = TextUnit(25F, TextUnitType.Sp),
            )
            Surface(
                modifier = Modifier.size(40.dp),
                onClick = onDelete,
                color = Color.Transparent,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_delete_sweep_24),
                    modifier = Modifier.padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = null,
                )
            }
            Surface(
                modifier = Modifier.size(40.dp),
                onClick = onShare,
                color = Color.Transparent,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_share_24),
                    modifier = Modifier.padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = null,
                )
            }
        }
        if (logs.value.isEmpty()) {
            Spacer(modifier = Modifier.height(64.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                text = stringResource(id = R.string.no_logs_message),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                modifier = Modifier.padding(8.dp),
                text = String.format("%s logs", logs.value.size.toCurrency(false))
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(logs.value) { log ->
                    Item(log = log)
                }
            }
        }
    }
}

@Composable
private fun Item(log: Log) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        val textColor = getColorByLevel(log.levelCode)
        Text(
            modifier = Modifier.width(30.dp),
            text = log.levelCode,
            textAlign = TextAlign.Center,
            color = textColor,
        )
        Text(
            text = ":",
            color = textColor,
        )
        Spacer(modifier = Modifier.size(8.dp, 0.dp))
        Text(
            text = log.message,
            color = textColor,
        )
    }
}

@Composable
private fun getColorByLevel(levelCode: String): Color {
    val level = Level.fromCode(levelCode)
    return when (level) {
        Level.INFO -> MaterialTheme.colorScheme.primary
        Level.DEBUG -> MaterialTheme.colorScheme.secondary
        Level.WARN -> MaterialTheme.colorScheme.tertiary
        Level.ERROR -> MaterialTheme.colorScheme.error
    }
}

private fun writeToFile(context: Context, text: String, fileName: String = "nebula_log.txt"): File {
    val file = File(context.cacheDir, fileName)
    FileWriter(file).use { writer ->
        writer.write(text)
    }
    return file
}

fun startIntentShareFile(context: Context, file: File, authority: String) {
    val uri = FileProvider.getUriForFile(context, authority, file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission to the recipient app
    }
    context.startActivity(Intent.createChooser(intent, "Share File"))
}

@Preview(
    showBackground = true,
    device = "id:small_phone",
)
@Composable
private fun Preview() {
    val sampleLogs = remember {
        mutableStateOf(
            List(10) { index ->
                Log().apply {
                    levelCode = Level.entries.random().code
                    message = "Sample log message #$index"
                }
            }
        )
    }

    NebulaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LogListScreen(sampleLogs)
        }
    }
}

@Preview(
    showBackground = true,
    device = "id:small_phone",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewNight() {
    val sampleLogs = remember {
        mutableStateOf(
            List(10) { index ->
                Log().apply {
                    levelCode = Level.entries.random().code
                    message = "Sample log message #$index"
                }
            }
        )
    }

    NebulaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LogListScreen(sampleLogs)
        }
    }
}

@Preview(
    showBackground = true,
    device = "id:small_phone",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewEmpty() {
    val sampleLogs = remember { mutableStateOf(listOf<Log>()) }

    NebulaTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            LogListScreen(sampleLogs)
        }
    }
}