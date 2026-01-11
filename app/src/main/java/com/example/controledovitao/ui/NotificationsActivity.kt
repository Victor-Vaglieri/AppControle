package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledovitao.databinding.NotificationsBinding
import com.example.controledovitao.ui.adapter.NotificationAdapter
import com.example.controledovitao.viewmodel.NotificationsViewModel

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: NotificationsBinding
    private lateinit var viewModel: NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = androidx.lifecycle.ViewModelProvider(this)[NotificationsViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupList()
        setupListener()
    }

    private fun setupList() {
        val list = viewModel.listNotifications
        binding.recyclerNotifications.layoutManager = LinearLayoutManager(this)
        binding.recyclerNotifications.adapter = NotificationAdapter(list)
    }

    private fun setupListener(){
        binding.switchPush.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updatePush(isChecked)
            if (isChecked) Toast.makeText(this, "Push ativo", Toast.LENGTH_SHORT).show()
        }

        binding.switchEmail.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateEmail(isChecked)
            if (isChecked) Toast.makeText(this, "Email ativo", Toast.LENGTH_SHORT).show()
        }
    }
}