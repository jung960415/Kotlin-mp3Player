package com.example.androidmp3player.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidmp3player.DB.DBHelper
import com.example.androidmp3player.DataClass.Music
import com.example.androidmp3player.Adapter.MusicRecyclerViewAdapter
import com.example.androidmp3player.databinding.FragmentGenreBinding

class GenreFragment(context: Context) : Fragment() {
    val mainContext = context
    lateinit var binding: FragmentGenreBinding
    val DB_NAME = "musicDB" //DB명
    val dbHelper = DBHelper(mainContext, DB_NAME, null, 1)
    var musicList: MutableList<Music>? = null
    lateinit var musicRecyclerViewAdapter: MusicRecyclerViewAdapter
    var genre: Int = 0
    val BALLADE = 0
    val HIPHOP = 1
    val DANCE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGenreBinding.inflate(inflater, container, false)
        if(dbHelper.selectBalladeMusic() != null){
            musicList = dbHelper.selectBalladeMusic()!!
        }else{
            musicList = null
        }

        musicRecyclerViewAdapter = MusicRecyclerViewAdapter(mainContext, musicList)

        //리사이클러뷰 어뎁터, 레이아웃매니저 설정
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.genreRecyclerView.addItemDecoration(DividerItemDecoration(activity, 1))
        binding.genreRecyclerView.layoutManager = linearLayoutManager
        binding.genreRecyclerView.adapter = musicRecyclerViewAdapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        musicList?.clear()

        if(genre == BALLADE){
            if(dbHelper.selectBalladeMusic() != null) musicList?.addAll(dbHelper.selectBalladeMusic()!!)
        }else if (genre == HIPHOP){
            if(dbHelper.selectHiphopMusic() != null) musicList?.addAll(dbHelper.selectHiphopMusic()!!)
        }else if (genre == DANCE){
            if(dbHelper.selectDanceMusic() != null) musicList?.addAll(dbHelper.selectDanceMusic()!!)
        }else{
            Log.d("jung", "")
        }

        musicRecyclerViewAdapter.notifyDataSetChanged()
    }
}