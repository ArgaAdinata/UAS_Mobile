package com.example.uas_mobile_argaadinata_514333.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bookmarkEntity: BookmarkEntity)

    @Query("SELECT * FROM bookmark_table ORDER BY timestamp DESC")
    fun getAllBookmarks(): LiveData<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmark_table ORDER BY timestamp DESC")
    fun getAllBookmarksSync(): List<BookmarkEntity>

    @Query("DELETE FROM bookmark_table WHERE api_id = :apiId")
    fun deleteByApiId(apiId: String)
}