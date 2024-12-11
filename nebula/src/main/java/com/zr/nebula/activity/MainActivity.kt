package com.zr.nebula.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.zr.nebula.R
import com.zr.nebula.databinding.ActivityLogListBinding
import com.zr.nebula.extension.toCurrency
import com.zr.repository.item.Log
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogListBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val list = listOf<Log>()
        val adapter = LogAdapter(list)
        binding.recyclerLog.adapter = adapter

        binding.buttonDelete.setOnClickListener {
            viewModel.deleteAll()
        }

        binding.buttonShare.setOnClickListener {
            viewModel.logs.value?.let { logs ->
                val text = logs.joinToString("\n\n")

                //writing log to file
                val path = File(filesDir, "nebula")
                val file = File(path, "nebula_log.txt")
                path.mkdirs()
                FileWriter(file).use { writer ->
                    writer.write("--- Logs Generated by Nebula Library ---\n")
                    writer.write("--- https://github.com/zihadrizkyef/Nebula ---\n\n\n")
                    writer.write(text)
                }

                //sharing file
                val authority = "${applicationContext.packageName}.nebula.fileprovider"
                val uri = FileProvider.getUriForFile(this, authority, file)
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Grant read permission to the recipient app
                }
                startActivity(Intent.createChooser(intent, "Share File"))
            }
        }

        viewModel.logs.observe(this) {
            binding.textCounter.text = getString(R.string.n_logs, it.size.toCurrency(false))
            adapter.logs = it
            adapter.notifyDataSetChanged()
        }
    }
}