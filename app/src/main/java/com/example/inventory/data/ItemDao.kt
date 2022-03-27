package com.example.inventory.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/*
* ItemテーブルのDaoクラス。
* データベース操作は実行に時間がかかる可能性があるため、コルーチンで別スレッドで実行する。
* そのため、suspend関数にする。
* ただし、SELECT文については、Roomが自動的にバックグラウンドでクエリを発行するのでsuspendにする必要はない（戻り値の型はFlowにする）。
* */
@Dao
interface ItemDao {
    /*
    * Itemデータ登録。
    * onConflictStrategy.IGNORE:主キーがすでにDBに存在する場合は、INSERTしない。
    * */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    /*
    * Itemデータ更新。
    * 渡されるEntityと同じキーを持つレコードが更新される
    * */
    @Update
    suspend fun update(item: Item)

    /*
    * Itemデータ削除。
    * 渡されるEntityと同じキーを持つレコードが削除される
    * */
    @Delete
    suspend fun delete(item: Item)

    /*
    * IDでItem取得。
    * Flow型の場合、RoomがDBとFlowの値を最新の状態に同期する。DBに変更があればUIなどのデータは直ぐ反映される。
    * また、Roomが自動的にバックグラウンドでクエリを発行するので、suspend関数にしなくて良い。
    * */
    @Query("SELECT * FROM item WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    /*
    * 全Item取得。
    * Flow型の場合、RoomがDBとFlowの値を最新の状態に同期する。DBに変更があればUIなどのデータは直ぐ反映される。
    * また、Roomが自動的にバックグラウンドでクエリを発行するので、suspend関数にしなくて良い。
    * */
    @Query("SELECT * FROM item")
    fun getItems(): Flow<List<Item>>
}