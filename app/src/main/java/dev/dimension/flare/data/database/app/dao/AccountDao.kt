package dev.dimension.flare.data.database.app.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.dimension.flare.data.database.app.model.DbAccount
import dev.dimension.flare.model.MicroBlogKey
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM DbAccount ORDER BY lastActive DESC LIMIT 1")
    fun getActiveAccount(): Flow<DbAccount?>

    @Query("SELECT * FROM DbAccount")
    fun getAllAccounts(): Flow<List<DbAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAccount(account: DbAccount)

    // set active account
    @Query("UPDATE DbAccount SET lastActive = :lastActive WHERE account_key = :accountKey")
    suspend fun setActiveAccount(accountKey: MicroBlogKey, lastActive: Long)

    @Delete
    suspend fun deleteAccount(account: DbAccount)
}