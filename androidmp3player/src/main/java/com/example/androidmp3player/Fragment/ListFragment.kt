package com.example.androidmp3player.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidmp3player.DB.DBHelper
import com.example.androidmp3player.Adapter.MusicRecyclerViewAdapter
import com.example.androidmp3player.databinding.FragmentListBinding

class ListFragment(context: Context) : Fragment() {
    val mainContext = context
    lateinit var  binding: FragmentListBinding
    val DB_NAME = "musicDB" //DB명
    val dbHelper = DBHelper(mainContext, DB_NAME, null, 1)
    var musicList = dbHelper.selectAllMusic()
    var musicRecyclerViewAdapter: MusicRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)

        musicRecyclerViewAdapter = MusicRecyclerViewAdapter(mainContext, musicList)

        //리사이클러뷰 어뎁터, 레이아웃매니저 설정
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.listRecyclerView.addItemDecoration(DividerItemDecoration(activity, 1))
        binding.listRecyclerView.layoutManager = linearLayoutManager
        binding.listRecyclerView.adapter = musicRecyclerViewAdapter

        return binding.root
    }
}