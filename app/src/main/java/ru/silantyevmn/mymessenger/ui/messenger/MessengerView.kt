package ru.silantyevmn.mymessenger.ui.messenger

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.silantyevmn.mymessenger.model.entity.ChatMessage
import ru.silantyevmn.mymessenger.model.entity.User

interface MessengerView : MvpView {
    fun showLoadingError(textError: String?)
    fun showLoadingSuccess()
    fun showLoading(text: String)
    fun showLoginActivity()
    fun updateAdapter(user: User, chatMessage: ChatMessage)
}