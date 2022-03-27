package com.example.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/*
* データベースクラス。Daoをアプリに提供する。
* */
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {
    /*
    * 他のクラスにDaoを提供するためのメソッド。Roomが実装するので抽象メソッド。
    * Daoを返すメソッドを複数定義可能。
    * */
    abstract fun itemDao(): ItemDao

    /*
    * アプリがデータベースクラスをインスタンス化して使えるようにするメソッド。データベースクラスはシングルトンとなる。
    * Applicationクラスで利用する。
    * */
    companion object {

        // あるスレッドが INSTANCE に加えた変更が、すぐに他のすべてのスレッドに反映。
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        /*
        * Databaseインスタンスを返すメソッド。それがnullだったらItemRoomDatabaseをインスタンス化して返却
        * nullでないならそのまま渡す。
        * インスタンス化時はマイグレーションの設定をしてビルドする。
        * マイグレーションの設定：DBに保存されているデータを保持したまま、テーブルの作成やカラムの変更を行う等の設定
        * fallbackToDestructiveMigration()：デバイス上に使用するバージョンのデータベースが見つからないときに初期状態のデータベースを作成
        * */
        fun getDatabase(context: Context): ItemRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}