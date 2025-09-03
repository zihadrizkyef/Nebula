package com.zr.nebula.activity

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zr.nebula.databinding.ItemLogBinding
import com.zr.nebula.data.item.Level
import com.zr.nebula.data.item.Log

internal class LogAdapter(var logs: List<Log>) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return holder
    }

    override fun getItemCount(): Int = logs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        val item = logs[position]

        binding.textLevel.text = item.levelCode
        binding.textMessage.text = item.message

        val textColor = getColorByLevel(item.levelCode)
        binding.textLevel.setTextColor(textColor)
        binding.textSeparator.setTextColor(textColor)
        binding.textMessage.setTextColor(textColor)
    }

    private fun getColorByLevel(levelCode: String): Int {
        val level = Level.fromCode(levelCode)
        return when (level) {
            Level.INFO -> Color.parseColor("#9E9E9E") // Gray 500 for info
            Level.DEBUG -> Color.parseColor("#2196F3") // Blue 500 for debug
            Level.WARN -> Color.parseColor("#FFC107") // Amber 500 for warnings
            Level.ERROR -> Color.parseColor("#F44336") // Red 500 for errors
        }
    }

}