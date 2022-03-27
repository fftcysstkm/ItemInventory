package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch

/*
* モノの在庫数のビューモデル。
* コンストラクタ引数にDaoを受け取り、DBのCRUD処理をUIに反映
* */
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    /*
    * DBにアイテムを登録する。
    * DBアクセスはメインスレッドではできないためコルーチンで行う。
    * そもそもdao.insert()もsuspend関数はsuspend関数またはコルーチン内からしか呼び出せない
    * */
    private fun insertItem(item: Item) {
        viewModelScope.launch {
            // insert()はsuspend関数として定義
            itemDao.insert(item)
        }
    }

    /*
    * 3つの文字列からItemクラスを返却
    * */
    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    /*
    * アイテムを登録する。
    * */
    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

    /*
    * IDでアイテムを一つ取得する。
    * */
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    /*
    * DBのアイテムを更新する。
    * */
    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    /*
    * アイテムの在庫を一つマイナスし、DB更新する。
    * */
    fun sellItem(item: Item) {
        if (item.quantityInStock > 0) {
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }

    /*
    * アイテムをDBから削除する
    * */
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }


    /*
    * 入力値空でないか検証する。
    * */
    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    /*
    * アイテムの在庫があるかチェック
    * */
    fun isStockAvailable(item: Item): Boolean {
        return item.quantityInStock > 0
    }
}

/*
* Daoを受け取ってViewModelのインスタンス化を可能にするFactoryクラス（定型、ボイラープレート）
* ViewModelはDaoを引数にとる普通のインスタンス化ができないため。
* */
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {

    // 引数modelClassが InventoryViewModel クラスと同じであることを確認してから、そのインスタンスを返却
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}