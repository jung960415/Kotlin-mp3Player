package com.example.androidmp3player.DB

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.androidmp3player.DataClass.Music

class DBHelper(context: Context, dbName: String, factory: SQLiteDatabase.CursorFactory?, version: Int): SQLiteOpenHelper(context, dbName, factory, version) {
    companion object{
        val TABLE_NAME = "musicTBL"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createQuery = "create table $TABLE_NAME(id TEXT primary key, title TEXT, artist TEXT, albumId TEXT, duration integer, genre TEXT, love integer, count integer)"
        db?.execSQL(createQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropQuery = "drop table if exists $TABLE_NAME"
        db?.execSQL(dropQuery)
        onCreate(db)
    }

    //음악 삽입
    fun insertMusic(music: Music): Boolean {
        var insertFlag = false
        val db = this.writableDatabase
        val insertQuery = "INSERT INTO $TABLE_NAME(id, title, artist, albumId, duration, genre, love, count)" +
                " VALUES(${music.id}, '${music.title}','${music.artist}','${music.albumId}',${music.duration},'${music.genre}',${music.love},${music.count})"

        try{
            db.execSQL(insertQuery)
            insertFlag = true
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }

        return insertFlag
    }

    //전부 가져오기
    fun selectAllMusic(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        val selectQuery = "select * from $TABLE_NAME"

        try {
            cursor = db.rawQuery(selectQuery, null)

            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val genre = cursor.getString(5)
                    val love = cursor.getInt(6)
                    val count = cursor.getInt(7)
                    val music = Music(id, title, artist, albumId, duration, genre, love, count)

                    musicList?.add(music)
                }
            }else{
                musicList = null
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.close()
            cursor?.close()
        }

        return musicList
    }

    //음악 수정
    fun updateMusic(music: Music): Boolean {
        var updateFlag = false
        val db = this.writableDatabase
        val updateQuery = "update $TABLE_NAME set id = ${music.id}, title = '${music.title}', artist = '${music.artist}'," +
                " albumId = '${music.albumId}', duration = ${music.duration}, genre = '${music.genre}', love = ${music.love}, count = ${music.count} " +
                "where id = ${music.id}"

        try {
            db.execSQL(updateQuery)
            updateFlag = true
        }catch (e: SQLException){
            e.printStackTrace()
        }finally {
            db.close()
        }

        return updateFlag
    }

    //좋아요 설정한 음악가져오기
    fun selectLoveMusic(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        val selectQuery = "select * from $TABLE_NAME where love = '1'"

        try {
            cursor = db.rawQuery(selectQuery, null)

            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val genre = cursor.getString(5)
                    val love = cursor.getInt(6)
                    val count = cursor.getInt(7)
                    val music = Music(id, title, artist, albumId, duration, genre, love, count)

                    musicList?.add(music)
                }
            }else{
                musicList = null
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.close()
            cursor?.close()
        }

        return musicList
    }

    fun deleteMusic(id: String?): Boolean {
        var deleteFlag = false
        val db = this.writableDatabase
        val deleteQuery = "delete from $TABLE_NAME where id = $id"

        try {
            db.execSQL(deleteQuery)
            deleteFlag = true
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }

        return deleteFlag
    }

    //발라드 장르 가져오기
    fun selectBalladeMusic(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        val selectQuery = "select * from $TABLE_NAME where genre = '발라드'"

        try {
            cursor = db.rawQuery(selectQuery, null)

            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val genre = cursor.getString(5)
                    val love = cursor.getInt(6)
                    val count = cursor.getInt(7)
                    val music = Music(id, title, artist, albumId, duration, genre, love, count)

                    musicList?.add(music)
                }
            }else{
                musicList = null
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.close()
            cursor?.close()
        }

        return musicList
    }

    //힙합 장르 가져오기
    fun selectHiphopMusic(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        val selectQuery = "select * from $TABLE_NAME where genre = '힙합'"

        try {
            cursor = db.rawQuery(selectQuery, null)

            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val genre = cursor.getString(5)
                    val love = cursor.getInt(6)
                    val count = cursor.getInt(7)
                    val music = Music(id, title, artist, albumId, duration, genre, love, count)

                    musicList?.add(music)
                }
            }else{
                musicList = null
            }

        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.close()
            cursor?.close()
        }

        return musicList
    }

    //댄스 장르 가져오기
    fun selectDanceMusic(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        val db = this.writableDatabase
        var cursor: Cursor? = null
        val selectQuery = "select * from $TABLE_NAME where genre = '댄스'"

        try {
            cursor = db.rawQuery(selectQuery, null)

            if(cursor.count > 0){
                while(cursor.moveToNext()){
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val genre = cursor.getString(5)
                    val love = cursor.getInt(6)
                    val count = cursor.getInt(7)
                    val music = Music(id, title, artist, albumId, duration, genre, love, count)

                    musicList?.add(music)
                }
            }else{
                musicList = null
            }
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            db.close()
            cursor?.close()
        }

        return musicList
    }
}