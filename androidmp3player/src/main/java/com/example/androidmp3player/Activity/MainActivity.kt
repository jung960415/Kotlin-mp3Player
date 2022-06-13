package com.example.androidmp3player.Activity

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.androidmp3player.DB.DBHelper
import com.example.androidmp3player.Fragment.GenreFragment
import com.example.androidmp3player.Fragment.ListFragment
import com.example.androidmp3player.Fragment.LoveFragment
import com.example.androidmp3player.DataClass.Music
import com.example.androidmp3player.R
import com.example.androidmp3player.Adapter.ViewPagerAdapter
import com.example.androidmp3player.databinding.ActivityMainBinding
import com.example.androidmp3player.databinding.TabButtonBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val REQUEST_READ = 200
    val DB_NAME = "musicDB" //DB명
    val BALLADE = 0
    val HIPHOP = 1
    val DANCE = 2
    var checkGenre: Int = 0 //장르 선택
    var selectGenre: Boolean = false // 장르 선택 유무
    var selectLove: Boolean = false // 좋아요 선택 유무
    var musicList: MutableList<Music>? = mutableListOf<Music>()
    val dbHelper = DBHelper(this, DB_NAME, null, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //승인이 되어있으면 음악파일을 가져오고, 승인이 안되었으면 재요청
        if(isPermitted()){
            //승인이 되었으면, 외부파일을 가져와서 컬렉션 프레임워크에 저장하고 리사이클러뷰에 출력
            startProcess()
        }else{
            //승인요청을 다시함 android.Manifest.permission.READ_EXTERNAL_STORAGE
            //요청이 승인되면 콜백함수로 승인결과값을 알려준다
            ActivityCompat.requestPermissions(this, permission, REQUEST_READ)
        }

        //뷰페이저 어뎁터 연결
        val viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        val listFragment = ListFragment(this)
        val genreFragment = GenreFragment(this)
        val loveFragment = LoveFragment(this)

        viewPagerAdapter.addFragment(listFragment)
        viewPagerAdapter.addFragment(genreFragment)
        viewPagerAdapter.addFragment(loveFragment)

        TabLayoutMediator(binding.tabLayout, binding.viewPager){tab, position ->
            tab.setCustomView(createTabItemView(position))
        }.attach()

        //탭레이아웃 이벤트설정
        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            //탭이 선택상태로 변경될때 호출
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        binding.tvLeft.isEnabled = false
                        binding.tvLeft.visibility = View.INVISIBLE
                        binding.tvRight.isEnabled = false
                        binding.tvRight.visibility = View.INVISIBLE
                        binding.tvCenter.text = "재생목록"
                        binding.tvCenter.isEnabled = false
                        selectLove = false
                        selectGenre = false
                    }
                    
                    1 -> {
                        binding.tvLeft.isEnabled = true
                        binding.tvLeft.visibility = View.VISIBLE
                        binding.tvRight.isEnabled = true
                        binding.tvRight.visibility = View.VISIBLE
                        binding.tvCenter.isEnabled = true
                        binding.tvCenter.setTextColor(Color.rgb(0, 0, 0))
                        binding.tvCenter.text = "발라드"
                        binding.tvLeft.text = "힙합"
                        binding.tvRight.text = "댄스"
                        selectGenre = true
                        selectLove = false

                        if(genreFragment.genre == HIPHOP){
                            binding.tvLeft.setTextColor(Color.rgb(0, 0, 0))
                            binding.tvCenter.setTextColor(Color.rgb(200, 200, 200))
                            binding.tvRight.setTextColor(Color.rgb(200, 200, 200))
                        }else if(genreFragment.genre == BALLADE){
                            binding.tvLeft.setTextColor(Color.rgb(200, 200, 200))
                            binding.tvCenter.setTextColor(Color.rgb(0, 0, 0))
                            binding.tvRight.setTextColor(Color.rgb(200, 200, 200))
                        }else if(genreFragment.genre == DANCE){
                            binding.tvLeft.setTextColor(Color.rgb(200, 200, 200))
                            binding.tvCenter.setTextColor(Color.rgb(200, 200, 200))
                            binding.tvRight.setTextColor(Color.rgb(0, 0, 0))
                        }
                    }
                    
                    2 -> {
                        binding.tvLeft.isEnabled = false
                        binding.tvLeft.visibility = View.INVISIBLE
                        binding.tvRight.isEnabled = false
                        binding.tvRight.visibility = View.INVISIBLE
                        binding.tvCenter.text = "좋아요"
                        binding.tvCenter.isEnabled = false
                        selectLove = true
                        selectGenre = false
                    }
                    
                }
            }

            //탭의 상태가 선택되지 않음으로 변경됐을 때 호출
            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            //이미 선택된 탭이 다시선택됐을 때 호출
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        //장르 텍스트뷰 이벤트설정
        binding.tvLeft.setOnClickListener {
            binding.tvLeft.setTextColor(Color.rgb(0, 0, 0))
            binding.tvCenter.setTextColor(Color.rgb(200, 200, 200))
            binding.tvRight.setTextColor(Color.rgb(200, 200, 200))
            genreFragment.genre = HIPHOP
            genreFragment.onResume()
            checkGenre = HIPHOP
        }

        binding.tvCenter.setOnClickListener {
            binding.tvLeft.setTextColor(Color.rgb(200, 200, 200))
            binding.tvCenter.setTextColor(Color.rgb(0, 0, 0))
            binding.tvRight.setTextColor(Color.rgb(200, 200, 200))
            genreFragment.genre = BALLADE
            genreFragment.onResume()
            checkGenre = BALLADE
        }

        binding.tvRight.setOnClickListener {
            binding.tvLeft.setTextColor(Color.rgb(200, 200, 200))
            binding.tvCenter.setTextColor(Color.rgb(200, 200, 200))
            binding.tvRight.setTextColor(Color.rgb(0, 0, 0))
            genreFragment.genre = DANCE
            genreFragment.onResume()
            checkGenre = DANCE
        }

        //초기화면 textView
        binding.tvLeft.isEnabled = false
        binding.tvLeft.visibility = View.INVISIBLE
        binding.tvRight.isEnabled = false
        binding.tvRight.visibility = View.INVISIBLE
        binding.tvCenter.isEnabled = false
    }

    //외부파일 읽기 승인요청
    private fun isPermitted(): Boolean = ContextCompat.checkSelfPermission(this, permission[0]) == PackageManager.PERMISSION_GRANTED

    //승인요청했을 때 승인결과에 대한 콜백함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_READ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //승인이 되었으면 startProces()함수로 작업을 진행한다
                startProcess()
            }else{
                Toast.makeText(this, "권한요청 승인 후 앱실행가능", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //퍼미션승인 후 실행
    private fun startProcess() {
        musicList = dbHelper.selectAllMusic()

        //테이블에 음악이 없으면
        if (musicList == null || musicList!!.size <= 0){
            //getMusicList로 외장메모리에서 음악 가져오고
            musicList = getMusicLists()
            musicList?.remove(musicList!!.get(0))
            musicList?.remove(musicList!!.get(0))

            val changeMusicList = changeMusicInfo(musicList)
            musicList?.clear()
            musicList = changeMusicList
            
            for (i in 0 until musicList!!.size){
                val music = musicList!![i]
                if(!dbHelper.insertMusic(music)){
                    Log.d("jung", "삽입오류 ${music.toString()}")
                }
            }
            Log.d("jung", "테이블에 없어서 getMusicList()에서 가져옴")
        }else{
            Log.d("jung", "테이블에 있어서 가져옴")
        }
    }

    //외부파일에서 음악정보 가져오기
    private fun getMusicLists(): MutableList<Music>? {
        //음악정보주소
        val listUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        //요청해야될 음악정보
        val proj = arrayOf(
            MediaStore.Audio.Media._ID,         //음악 id
            MediaStore.Audio.Media.TITLE,       //음악 제목
            MediaStore.Audio.Media.ARTIST,      //가수이름
            MediaStore.Audio.Media.ALBUM_ID,    //음악이미지
            MediaStore.Audio.Media.DURATION     //음악시간
        )

        //컨텐트 리졸버 쿼리에 Uri, 요청할 음악정보를 요구하고 결과값을 cursor로 반환받는다
        val cursor = contentResolver.query(listUri, proj, null, null, null)
        val musicList: MutableList<Music> = mutableListOf<Music>()

        //음악이 null이 아닐때
        while(cursor?.moveToNext() == true){
            val id = cursor.getString(0)
            val title = cursor.getString(1).replace("'", "")
            val artist = cursor.getString(2).replace("'", "")
            val albumId = cursor.getString(3)
            val duration = cursor.getLong(4)
            
            val music = Music(id, title, artist, albumId, duration, null, 0, 0)
            musicList.add(music)
        }

        cursor?.close()
        return musicList
    }

    //텝레이아웃 position마다 이미지, 텍스트뷰 설정
    private fun createTabItemView(position: Int): View? {
        val tabButtonBinding = TabButtonBinding.inflate(layoutInflater)
        when(position){
            0 -> {
                tabButtonBinding.ivTabLogo.setImageResource(R.drawable.list_24)
                tabButtonBinding.tvTabLogoName.text = "재생목록"
            }
            
            1 -> {
                tabButtonBinding.ivTabLogo.setImageResource(R.drawable.genre_24)
                tabButtonBinding.tvTabLogoName.text = "장르"
            }
            
            2 -> {
                tabButtonBinding.ivTabLogo.setImageResource(R.drawable.red_heart)
                tabButtonBinding.tvTabLogoName.text = "좋아요"
            }
            else -> tabButtonBinding.root
        }

        return tabButtonBinding.root
    }

    private fun changeMusicInfo(musicList: MutableList<Music>?): MutableList<Music>? {
        val changeMusicList: MutableList<Music>? = mutableListOf<Music>()
        
        musicList?.get(0)?.genre = "댄스"
        musicList?.get(1)?.genre = "댄스"
        musicList?.get(2)?.genre = "발라드"
        musicList?.get(3)?.genre = "댄스"
        musicList?.get(4)?.genre = "발라드"
        musicList?.get(5)?.genre = "발라드"
        musicList?.get(6)?.genre = "댄스"
        musicList?.get(7)?.genre = "댄스"
        musicList?.get(8)?.genre = "댄스"
        musicList?.get(9)?.genre = "댄스"
        musicList?.get(10)?.genre = "댄스"
        musicList?.get(11)?.genre = "발라드"
        musicList?.get(12)?.genre = "댄스"
        musicList?.get(13)?.genre = "댄스"
        musicList?.get(14)?.genre = "댄스"
        musicList?.get(15)?.genre = "댄스"
        musicList?.get(16)?.genre = "댄스"
        musicList?.get(17)?.genre = "발라드"
        musicList?.get(18)?.genre = "발라드"
        musicList?.get(19)?.genre = "발라드"
        musicList?.get(20)?.genre = "발라드"
        musicList?.get(21)?.genre = "발라드"
        musicList?.get(22)?.genre = "발라드"
        musicList?.get(23)?.genre = "발라드"
        musicList?.get(24)?.genre = "발라드"
        musicList?.get(25)?.genre = "댄스"
        musicList?.get(26)?.genre = "댄스"
        musicList?.get(27)?.genre = "댄스"
        musicList?.get(28)?.genre = "발라드"
        musicList?.get(29)?.genre = "발라드"
        musicList?.get(30)?.genre = "댄스"
        musicList?.get(31)?.genre = "댄스"
        musicList?.get(32)?.genre = "발라드"
        musicList?.get(33)?.genre = "댄스"
        musicList?.get(34)?.genre = "댄스"
        musicList?.get(35)?.genre = "발라드"
        musicList?.get(36)?.genre = "댄스"
        musicList?.get(37)?.genre = "발라드"
        musicList?.get(38)?.genre = "댄스"
        musicList?.get(39)?.genre = "발라드"
        musicList?.get(40)?.genre = "발라드"
        musicList?.get(41)?.genre = "발라드"
        musicList?.get(42)?.genre = "힙합"
        musicList?.get(43)?.genre = "발라드"
        musicList?.get(44)?.genre = "발라드"
        musicList?.get(45)?.genre = "발라드"
        musicList?.get(46)?.genre = "발라드"
        musicList?.get(47)?.genre = "발라드"
        musicList?.get(48)?.genre = "발라드"
        musicList?.get(49)?.genre = "힙합"
        musicList?.get(50)?.genre = "발라드"
        musicList?.get(51)?.genre = "댄스"
        musicList?.get(52)?.genre = "댄스"
        musicList?.get(53)?.genre = "댄스"
        musicList?.get(54)?.genre = "댄스"
        musicList?.get(55)?.genre = "발라드"
        musicList?.get(56)?.genre = "발라드"
        musicList?.get(57)?.genre = "댄스"
        musicList?.get(58)?.genre = "발라드"
        musicList?.get(59)?.genre = "힙합"
        musicList?.get(60)?.genre = "댄스"
        musicList?.get(61)?.genre = "발라드"
        musicList?.get(62)?.genre = "힙합"
        musicList?.get(63)?.genre = "댄스"
        musicList?.get(64)?.genre = "발라드"
        musicList?.get(65)?.genre = "발라드"
        musicList?.get(66)?.genre = "발라드"
        musicList?.get(67)?.genre = "댄스"
        musicList?.get(68)?.genre = "댄스"
        musicList?.get(69)?.genre = "댄스"
        musicList?.get(70)?.genre = "발라드"
        musicList?.get(71)?.genre = "발라드"
        musicList?.get(72)?.genre = "댄스"
        musicList?.get(73)?.genre = "발라드"
        musicList?.get(74)?.genre = "힙합"
        musicList?.get(75)?.genre = "댄스"
        musicList?.get(76)?.genre = "댄스"
        musicList?.get(77)?.genre = "발라드"
        musicList?.get(78)?.genre = "댄스"
        musicList?.get(79)?.genre = "댄스"
        musicList?.get(80)?.genre = "발라드"
        musicList?.get(81)?.genre = "댄스"
        musicList?.get(82)?.genre = "힙합"
        musicList?.get(83)?.genre = "댄스"
        musicList?.get(84)?.genre = "힙합"
        musicList?.get(85)?.genre = "발라드"
        musicList?.get(86)?.genre = "발라드"
        musicList?.get(87)?.genre = "댄스"
        musicList?.get(88)?.genre = "발라드"
        musicList?.get(89)?.genre = "댄스"
        musicList?.get(90)?.genre = "발라드"
        musicList?.get(91)?.genre = "힙합"
        musicList?.get(92)?.genre = "댄스"
        musicList?.get(93)?.genre = "댄스"
        musicList?.get(94)?.genre = "댄스"
        musicList?.get(95)?.genre = "댄스"
        musicList?.get(96)?.genre = "댄스"
        musicList?.get(97)?.genre = "댄스"
        musicList?.get(98)?.genre = "발라드"
        musicList?.get(99)?.genre = "발라드"

        changeMusicList?.addAll(musicList!!)

        return changeMusicList
    }

}