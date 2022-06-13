package com.example.androidmp3player.Activity

import android.graphics.Bitmap
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import com.example.androidmp3player.DB.DBHelper
import com.example.androidmp3player.DataClass.Music
import com.example.androidmp3player.R
import com.example.androidmp3player.databinding.ActivityPlayMusicBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlayMusicActivity : AppCompatActivity() {
    val binding by lazy { ActivityPlayMusicBinding.inflate(layoutInflater) }
    //음악 변수
    private var mediaPlayer: MediaPlayer? = null
    //음악 정보 객체 변수
    private var music: Music? = null
    //앨범 아트 사이즈
    private val ALBUMART_SIZE = 250
    //코루틴 스코프 런치
    private var playJob: Job? = null
    //코루틴 스레드
    val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
    //DB
    val DB_NAME = "musicDB" //DB명
    val dbHelper = DBHelper(this, DB_NAME, null, 1)
    //장르 상수
    val BALLADE = 0
    val HIPHOP = 1
    val DANCE = 2
    //음악리스트
    var musicList: MutableList<Music>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var checkLove = false   //좋아요 체크
        val allMusicList = dbHelper.selectAllMusic()
        val hiphopMusicList = dbHelper.selectHiphopMusic()
        val balladeMusicList = dbHelper.selectBalladeMusic()
        val danceMusicList = dbHelper.selectDanceMusic()
        val loveMusicList = dbHelper.selectLoveMusic()

        //인텐트를 통해 RecyclerAdapter에서 처리한 음악을 받아옴
        var position = intent.getIntExtra("position", 0)
        val checkGenre: Int = intent.getIntExtra("checkGenre", 1)
        val selectLove = intent.getBooleanExtra("selectLove", true)
        val selectGenre = intent.getBooleanExtra("selectGenre", true)

        //재생목록 음악
        if(!selectLove && !selectGenre){
            musicList = allMusicList
            music = musicList?.get(position)
        }

        //장르 음악
        if(selectGenre){
            if(checkGenre == HIPHOP){
                musicList = hiphopMusicList
                music = musicList?.get(position)
            }else if(checkGenre == BALLADE){
                musicList = balladeMusicList
                music = musicList?.get(position)
            }else if(checkGenre == DANCE){
                musicList = danceMusicList
                music = musicList?.get(position)
            }
        }

        //좋아요 음악
        if(selectLove){
            musicList = loveMusicList
            music = musicList?.get(position)
        }

        binding.tvCount.text = "${music?.count.toString()}회 재생"

        //좋아요면 빨간하트, 좋아요아니면 빈하트
        if(music?.love == 1){
            binding.ivLove.setImageResource(R.drawable.red_heart)
            checkLove = true
        }else{
            binding.ivLove.setImageResource(R.drawable.empty_heart)
            checkLove = false
        }

        if(music != null){
            //뷰 설정
            binding.tvPlayTitle.text = music?.title
            binding.tvPlayArtist.text = music?.artist
            binding.tvStartDuration.text = "00:00"
            binding.tvEndDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)

            val bitmap: Bitmap? = music?.getAlbumImage(this, ALBUMART_SIZE)
            if (bitmap != null) binding.ivPlayAlbumArt.setImageBitmap(bitmap)
            else binding.ivPlayAlbumArt.setImageResource(R.drawable.music_video_24)

            //음원 생성 및 재생
            mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())
            binding.seekBar.max = music?.duration!!.toInt()

            //seekBar 이벤트 설정으로 노래와 동기화 처리 - ChangeListener: 움직이기만 하면 이벤트발생
            binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                //seekBar를 터치해서 이동할 때 생기는 이벤트
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    //progress: 진행상태, fromUser: 유저에 의해 이동했다면 true, 프로그램에 의해 이동하면 false
                    if(fromUser){
                        //seekTo(progress): progress로 이동
                        mediaPlayer?.seekTo(progress)
                    }
                }

                //seekBar를 터치하는 순간 발생하는 이벤트
                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                //seekBar에서 손을 떼는 순간 발생하는 이벤트
                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })
        }

        //좋아요 이미지클릭 이벤트
        binding.ivLove.setOnClickListener {
            if(!checkLove){
                binding.ivLove.setImageResource(R.drawable.red_heart)
                music?.love = 1
                dbHelper.updateMusic(music!!)
            }else{
                binding.ivLove.setImageResource(R.drawable.empty_heart)
                music?.love = 0
                dbHelper.updateMusic(music!!)
            }
        }

        //뒤로가기 이미지클릭 이벤트
        binding.ivBack.setOnClickListener {
            //음악정지
            mediaPlayer?.stop()
            //코루틴 해제
            playJob?.cancel()
            finish()
        }

        //재생, 정지 이미지클릭 이벤트
        binding.ivPlay.setOnClickListener {
            if(mediaPlayer!!.isPlaying){
                mediaPlayer?.pause()
                binding.ivPlay.setImageResource(R.drawable.play_24)
            }else{
                //음악이 일시정지 상태라면 터치했을 때 시작
                mediaPlayer?.start()
                binding.ivPlay.setImageResource(R.drawable.pause_24)

                music?.count = music?.count?.plus(1)
                dbHelper.updateMusic(music!!)

                binding.tvCount.text = "${music?.count.toString()}회 재생"
            }

            //스레드
            playJob = backgroundScope.launch {
                while (mediaPlayer!!.isPlaying){
                    runOnUiThread {
                        val currentPosition = mediaPlayer!!.currentPosition
                        binding.seekBar.progress = currentPosition
                        binding.tvStartDuration.text = SimpleDateFormat("mm:ss").format(currentPosition)
                        binding.tvEndDuration.text = SimpleDateFormat("mm:ss").format(music?.duration!! - currentPosition)
                    }
                    try {
                        delay(300)
                    }catch (e: Exception) {
                        Log.d("jung", "delay error: ${e.printStackTrace()}")
                    }
                }//end of while

                //음악이 끝났으니 seekBar초기화
                runOnUiThread{
                    if (mediaPlayer!!.currentPosition >= binding.seekBar.max - 1000){
                        binding.seekBar.progress = 0
                        binding.tvStartDuration.text = "00:00"
                        binding.tvEndDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)
                    }
                    binding.ivPlay.setImageResource(R.drawable.play_24)
                }
            }//end of backgroundScope.launch
        }

        //이전노래 이미지클릭 이벤트
        binding.ivPrevSong.setOnClickListener {
            //음악정지
            mediaPlayer?.stop()
            //코루틴 해제
            playJob?.cancel()

            binding.seekBar.progress = 0

            if(position != 0){
                position--
                music = musicList?.get(position)
            }else{
                music = musicList?.get(musicList?.size!! - 1)
                position = musicList!!.size - 1
            }

            binding.tvCount.text = "${music?.count.toString()}회 재생"

            //좋아요면 빨간하트, 좋아요아니면 빈하트
            if(music?.love == 1){
                binding.ivLove.setImageResource(R.drawable.red_heart)
                checkLove = true
            }else{
                binding.ivLove.setImageResource(R.drawable.empty_heart)
                checkLove = false
            }

            if(music != null){
                //뷰 설정
                binding.tvPlayTitle.text = music?.title
                binding.tvPlayArtist.text = music?.artist
                binding.tvStartDuration.text = "00:00"
                binding.tvEndDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)

                val bitmap: Bitmap? = music?.getAlbumImage(this, ALBUMART_SIZE)
                if (bitmap != null) binding.ivPlayAlbumArt.setImageBitmap(bitmap)
                else binding.ivPlayAlbumArt.setImageResource(R.drawable.music_video_24)

                //음원 생성 및 재생
                mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())
                binding.seekBar.max = music?.duration!!.toInt()

                //seekBar 이벤트 설정으로 노래와 동기화 처리 - ChangeListener: 움직이기만 하면 이벤트발생
                binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                    //seekBar를 터치해서 이동할 때 생기는 이벤트
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        //progress: 진행상태, fromUser: 유저에 의해 이동했다면 true, 프로그램에 의해 이동하면 false
                        if(fromUser){
                            //seekTo(progress): progress로 이동
                            mediaPlayer?.seekTo(progress)
                        }
                    }

                    //seekBar를 터치하는 순간 발생하는 이벤트
                    override fun onStartTrackingTouch(p0: SeekBar?) {

                    }

                    //seekBar에서 손을 떼는 순간 발생하는 이벤트
                    override fun onStopTrackingTouch(p0: SeekBar?) {

                    }
                })
            }
        }

        //다음노래 이미지클릭 이벤트
        binding.ivNextSong.setOnClickListener {
            //음악정지
            mediaPlayer?.stop()
            //코루틴 해제
            playJob?.cancel()

            binding.seekBar.progress = 0

            if(position != musicList!!.size - 1){
                position++
                music = musicList?.get(position)
            }else{
                position = 0
                music = musicList?.get(position)
            }

            binding.tvCount.text = "${music?.count.toString()}회 재생"

            //좋아요면 빨간하트, 좋아요아니면 빈하트
            if(music?.love == 1){
                binding.ivLove.setImageResource(R.drawable.red_heart)
                checkLove = true
            }else{
                binding.ivLove.setImageResource(R.drawable.empty_heart)
                checkLove = false
            }

            if(music != null){
                //뷰 설정
                binding.tvPlayTitle.text = music?.title
                binding.tvPlayArtist.text = music?.artist
                binding.tvStartDuration.text = "00:00"
                binding.tvEndDuration.text = SimpleDateFormat("mm:ss").format(music?.duration)

                val bitmap: Bitmap? = music?.getAlbumImage(this, ALBUMART_SIZE)
                if (bitmap != null) binding.ivPlayAlbumArt.setImageBitmap(bitmap)
                else binding.ivPlayAlbumArt.setImageResource(R.drawable.music_video_24)

                //음원 생성 및 재생
                mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())
                binding.seekBar.max = music?.duration!!.toInt()

                //seekBar 이벤트 설정으로 노래와 동기화 처리 - ChangeListener: 움직이기만 하면 이벤트발생
                binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                    //seekBar를 터치해서 이동할 때 생기는 이벤트
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        //progress: 진행상태, fromUser: 유저에 의해 이동했다면 true, 프로그램에 의해 이동하면 false
                        if(fromUser){
                            //seekTo(progress): progress로 이동
                            mediaPlayer?.seekTo(progress)
                        }
                    }

                    //seekBar를 터치하는 순간 발생하는 이벤트
                    override fun onStartTrackingTouch(p0: SeekBar?) {

                    }

                    //seekBar에서 손을 떼는 순간 발생하는 이벤트
                    override fun onStopTrackingTouch(p0: SeekBar?) {

                    }
                })
            }
        }
    }
}