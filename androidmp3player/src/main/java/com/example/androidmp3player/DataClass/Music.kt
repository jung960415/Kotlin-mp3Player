package com.example.androidmp3player.DataClass

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import java.io.IOException
import java.io.Serializable

class Music(id: String, title: String?, artist: String?, albumId: String?, duration: Long?, genre: String?, love: Int?, count: Int?): Serializable {

    var id: String = ""
    var title: String? = null
    var artist: String? = null
    var albumId: String? = null
    var duration: Long? = 0L
    var genre: String? = null
    var love: Int? = 0
    var count: Int? = 0

    init {
        this.id = id
        this.title = title
        this.artist = artist
        this.albumId = albumId
        this.duration = duration
        this.genre = genre
        this.love = love
        this.count = count
    }

    //컨텐트리졸버를 이용해서 앨범정보를 가져오기 위한 경로 Uri(이미지정보)
    fun getAlbumUri(): Uri{
        return Uri.parse("content://media/external/audio/albumart/"+albumId)
    }

    //음악정보를 가져오기 위한 경로 Uri 얻기(음악정보)
    fun getMusicUri(): Uri{
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    //해당되는 음악에 이미지를 내가 원하는 사이즈로 비트맵 만들어돌려주기
    fun getAlbumImage(context: Context, albumImageSize: Int): Bitmap?{
        val contentResolver: ContentResolver = context.getContentResolver()
        //앨범 경로
        val uri = getAlbumUri()
        //앨범에 대한 정보를 저장하기 위함
        val options = BitmapFactory.Options()

        if(uri != null){
            var parcelFileDescriptor: ParcelFileDescriptor? = null

            try {
                //외부파일에 있는 이미지파일을 가져오기 위한 스트림
                parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")

                var bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor!!.fileDescriptor, null, options)

                //비트맵을 가져와서 사이즈 결정(원본이미지 사이즈가 내가 원하는 사이즈하고 맞지 않을경우 내가 원하는 사이즈로 맞춤)
                if(bitmap != null){
                    if(options.outHeight !== albumImageSize || options.outWidth !== albumImageSize){
                        val tempBitmap = Bitmap.createScaledBitmap(bitmap, albumImageSize, albumImageSize, true)
                        bitmap.recycle()
                        bitmap = tempBitmap
                    }
                    return bitmap
                }
            }catch (e: Exception){

            }finally {
                try {
                    parcelFileDescriptor?.close()
                }catch (e: IOException){

                }
            }
        }

        return null
    }
}