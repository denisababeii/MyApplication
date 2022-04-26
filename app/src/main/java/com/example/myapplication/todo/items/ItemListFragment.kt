package com.example.myapplication.todo.items

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.SensorsWindow
import com.example.myapplication.auth.data.AuthRepository
import com.example.myapplication.core.TAG
import com.example.myapplication.databinding.FragmentItemListBinding

class ItemListFragment : Fragment() {
    private var _binding: FragmentItemListBinding? = null
    private lateinit var itemListAdapter: ItemListAdapter
    private lateinit var itemsModel: ItemListViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated")
        if (!AuthRepository.isLoggedIn) {
            findNavController().navigate(R.id.FragmentLogin)
            return;
        }
        setupItemList()

//        binding.fab2.setOnClickListener {
//            Log.v(TAG, "sensor")
//            val intent = Intent(context,SensorsWindow::class.java)
//            startActivity(intent)
//        }

        binding.fab.setOnClickListener {
            Log.v(TAG, "add new item")
            findNavController().navigate(R.id.ItemEditFragment)
        }
        //changeFabPositionByObjectAnimator()
    }

//    private fun changeFabPositionByObjectAnimator() {
//        ObjectAnimator.ofFloat(binding.fab2, "translationX", 200f).apply {
//            duration = 5000
//            start()
//        }
//        ObjectAnimator.ofFloat(binding.fab, "translationX", -200f).apply {
//            duration = 5000
//            start()
//        }
//    }

    private fun setupItemList() {
        itemListAdapter = ItemListAdapter(this)
        binding.itemList.adapter = itemListAdapter
        itemsModel = ViewModelProvider(this).get(ItemListViewModel::class.java)
        itemsModel.items.observe(viewLifecycleOwner, { value ->
            Log.i(TAG, "update items")
            itemListAdapter.items = value
        })
        itemsModel.loading.observe(viewLifecycleOwner, { loading ->
            Log.i(TAG, "update loading")
            binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        })
        itemsModel.loadingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.i(TAG, "update loading error")
                val message = "Loading exception ${exception.message}"
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        })
        itemsModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView")
        _binding = null
    }
}