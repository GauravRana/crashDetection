package com.example.biker112.ui.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.biker112.R
import com.example.biker112.ui.utils.BaseFragment

class SupportFragment : BaseFragment() {

    private lateinit var toolsViewModel: SupportViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolsViewModel =
            ViewModelProviders.of(this).get(SupportViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        toolsViewModel.text.observe(this, Observer {
            textView.text = it
        })

        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:"+"121"))
        context?.startActivity(intent)
        findNavController().popBackStack()
        return root
    }



}