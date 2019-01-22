package ru.silantyevmn.mymessenger.ui.chat

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.silantyevmn.mymessenger.model.entity.User

@StateStrategyType(SingleStateStrategy::class)
interface ChatView : MvpView {
    fun setTitleToolbar(user: User)
    fun setScrollRecycler()
    fun setTextMessagesClear()
    fun showLoading(text: String)
    fun onSuccess()
    fun onError(textError: String)
    fun updateStatus(position: Int)
    fun updateAdapter()
}