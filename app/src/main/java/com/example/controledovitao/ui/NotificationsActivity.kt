package com.example.controledovitao.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.controledovitao.databinding.NotificationsBinding
import com.example.controledovitao.ui.adapter.NotificationAdapter
import com.example.controledovitao.viewmodel.NotificationsViewModel

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: NotificationsBinding
    private lateinit var viewModel: NotificationsViewModel
    private val adapter = NotificationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[NotificationsViewModel::class.java]

        TopBarHelper.setupTopBar(this, binding.topBar)

        setupRecyclerView()
        setupObservers()
        setupListener()
    }

    private fun setupRecyclerView() {
        binding.recyclerNotifications.layoutManager = LinearLayoutManager(this)
        binding.recyclerNotifications.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.notifications.observe(this) { lista ->
            adapter.updateList(lista)
        }

        viewModel.isPushEnabled.observe(this) { isEnabled ->
            if (binding.switchPush.isChecked != isEnabled) {
                binding.switchPush.isChecked = isEnabled
            }
        }

        viewModel.isEmailEnabled.observe(this) { isEnabled ->
            if (binding.switchEmail.isChecked != isEnabled) {
                binding.switchEmail.isChecked = isEnabled
            }
        }
    }

    private fun setupListener(){
        binding.switchPush.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updatePush(isChecked)
            if (isChecked) Toast.makeText(this, "Push ativado", Toast.LENGTH_SHORT).show()
        }

        binding.switchEmail.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateEmail(isChecked)
            if (isChecked) Toast.makeText(this, "Email ativado", Toast.LENGTH_SHORT).show()
        }
    }
}