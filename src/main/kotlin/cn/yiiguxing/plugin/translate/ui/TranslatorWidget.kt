package cn.yiiguxing.plugin.translate.ui

import cn.yiiguxing.plugin.translate.Settings
import cn.yiiguxing.plugin.translate.SettingsChangeListener
import cn.yiiguxing.plugin.translate.action.SwitchTranslatorAction
import cn.yiiguxing.plugin.translate.ui.settings.TranslationEngine
import cn.yiiguxing.plugin.translate.util.TranslateService
import com.intellij.ide.DataManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.Consumer
import com.intellij.util.messages.MessageBusConnection
import java.awt.Component
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.Icon

/**
 * TranslatorWidget
 */
class TranslatorWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.IconPresentation {

    private var statusBar: StatusBar? = null

    @Suppress("FunctionName")
    override fun ID(): String = ID

    override fun getTooltipText(): String = TranslateService.translator.name

    override fun getIcon(): Icon = TranslateService.translator.icon

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        project.messageBus.connect(this).subscribeToSettingsChangeEvents()
    }

    override fun getClickConsumer(): Consumer<MouseEvent> = Consumer {
        getPopupStep(it.component).apply {
            val at = Point(0, -content.preferredSize.height)
            show(RelativePoint(it.component, at))
        }
    }

    private fun getPopupStep(component: Component): ListPopup {
        val context = DataManager.getInstance().getDataContext(component)
        return SwitchTranslatorAction.createTranslatorPopup(context)
    }

    override fun dispose() {
        statusBar = null
    }

    private fun MessageBusConnection.subscribeToSettingsChangeEvents() {
        subscribe(SettingsChangeListener.TOPIC, object : SettingsChangeListener {
            override fun onTranslatorChanged(settings: Settings, translationEngine: TranslationEngine) {
                statusBar?.updateWidget(ID())
            }
        })
    }

    companion object {
        const val ID = "TranslatorWidget"
    }
}