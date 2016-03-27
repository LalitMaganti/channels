package co.fusionx.channels.presenter

import android.os.Bundle
import co.fusionx.channels.activity.MainActivity

class ActionsPresenter(override val activity: MainActivity) : Presenter {
    override val id: String
        get() = "actions"

    override fun setup(savedState: Bundle?) {

    }
}