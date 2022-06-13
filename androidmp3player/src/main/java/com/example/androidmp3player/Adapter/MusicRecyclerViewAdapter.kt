package com.example.androidmp3player.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidmp3player.Activity.MainActivity
import com.example.androidmp3player.Activity.PlayMusicActivity
import com.example.androidmp3player.DataClass.Music
import com.example.androidmp3player.R
import com.example.androidmp3player.databinding.MusicItemViewBinding

class MusicRecyclerViewAdapter(val context: Context, val musicList: MutableList<Music>?): RecyclerView.Adapter<MusicRecyclerViewAdapter.CustomViewHolder>() {
    //이미지 사이즈
    var ALBUM_IMAGE_SIZE = 50
    val mainContext = context as MainActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = MusicItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        val music = musicList?.get(holder.adapterPosition)

        binding.tvArtist.text = music?.artist // 가수명
        binding.tvTitle.text = music?.title // 노래제목
        val bitmap: Bitmap? = music?.getAlbumImage(context, ALBUM_IMAGE_SIZE)
        if(bitmap != null){
            binding.ivAlbumImage.setImageBitmap(bitmap)
            binding.ivAlbumImage.clipToOutline = true
        }else{
            //앨범이미지가 없을경우 디폴트 이미지를 붙여넣는다.
            binding.ivAlbumImage.setImageResource(R.drawable.music_video_24)
        }

        //항목클릭시 재생화면으로 넘어감 checkGenre, selectLove, selectGenre, position을 가지고넘어감
        binding.root.setOnClickListener{
            //액티비티로 음악정보를 넘겨서 음악을 재생해주는 액티비티 설계
            val intent = Intent(binding.root.context, PlayMusicActivity::class.java)
            intent.putExtra("checkGenre", mainContext.checkGenre)
            intent.putExtra("selectLove", mainContext.selectLove)
            intent.putExtra("selectGenre", mainContext.selectGenre)
            intent.putExtra("position", holder.adapterPosition)
            binding.root.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = musicList?.size?: 0

    class CustomViewHolder(val binding: MusicItemViewBinding):RecyclerView.ViewHolder(binding.root)
}