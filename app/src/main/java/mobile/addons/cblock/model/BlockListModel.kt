package mobile.addons.cblock.model

import mobile.addons.cblock.data.BlockListItem
import mobile.addons.cblock.data.BlockListItemDao
import mobile.addons.cblock.data.CallLogItem
import mobile.addons.cblock.data.DaoSession
import mobile.addons.cblock.ext.log
import mobile.addons.cblock.scope.ViewScope
import mobile.addons.cblock.service.BlockService
import mobile.addons.cblock.ui.view.add.CallLogAdapter
import rx.Observable
import javax.inject.Inject

/**
 * Provide data for block list
 */

@ViewScope
class BlockListModel @Inject constructor(private val daoSession: DaoSession) {

    private val blockListDao = daoSession.blockListItemDao

    /**
     * Add phone number (incoming or missed) to block list
     * If number already exists in list  - it will not added

     * @param logItem incoming or missed call
     */
    fun addNumber(logItem: CallLogItem) = daoSession.runInTx {
        var dbItem = blockListDao.queryBuilder()
                .where(BlockListItemDao.Properties.PhoneNumber.eq(logItem.phoneNumber))
                .limit(1)
                .unique()
        if (dbItem == null) {
            dbItem = BlockListItem()
            dbItem.phoneNumber = logItem.phoneNumber
            dbItem.date = logItem.date
            dbItem.name = logItem.name
            blockListDao.insert(dbItem)
            log("added: ${logItem.phoneNumber}")
        }
    }

    /**
     * Delete phone number from block list

     * @param itemId db item id
     */
    fun deleteNumber(itemId: Long) = blockListDao.deleteByKeyInTx(itemId)

    /**
     * @return all blocked numbers from DB
     */
    fun getNumbersList() : List<BlockListItem> = blockListDao.queryBuilder()
            .orderDesc(BlockListItemDao.Properties.Date).list()

    /**
     * @return all blocked numbers observable list
     */
    fun getBlockList(): Observable<List<BlockListItem>> = Observable.fromCallable { getNumbersList() }

    /**
     * @return set of blocking phones, used in [CallLogAdapter] and [BlockService]
     */
    fun getBlockedPhones(): Observable<HashSet<String>> = getBlockList().map { getBlockedPhones(it) }

    /**
     * @return set of blocking phones, used in [BlockService]
     */
    fun getBlockedPhones(items: List<BlockListItem>): HashSet<String> = items.map { it.phoneNumber }.toHashSet()

    /**
     * remove all numbers from block list
     */
    fun clearBlockList() = blockListDao.deleteAll()

}
