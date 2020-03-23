package br.com.astrosoft.framework.view

import com.github.appreciated.app.layout.addons.notification.DefaultNotificationHolder
import com.github.appreciated.app.layout.addons.notification.component.NotificationButton
import com.github.appreciated.app.layout.addons.notification.entity.DefaultNotification
import com.github.appreciated.app.layout.annotations.Caption
import com.github.appreciated.app.layout.annotations.Icon
import com.github.appreciated.app.layout.component.appbar.AppBarBuilder
import com.github.appreciated.app.layout.component.applayout.AbstractLeftAppLayoutBase
import com.github.appreciated.app.layout.component.applayout.TopLayouts.TopLarge
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder
import com.github.appreciated.app.layout.component.menu.top.TopMenuComponent
import com.github.appreciated.app.layout.component.menu.top.builder.TopAppMenuBuilder
import com.github.appreciated.app.layout.component.menu.top.item.TopNavigationItem
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.icon.VaadinIcon.BELL
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.component.page.Viewport
import kotlin.reflect.KClass

@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
open class KAppLayoutTopLargeLayout(): AppLayoutRouterLayout<TopLarge?>() {
  val notifications = DefaultNotificationHolder().apply {
    this.addClickListener {notification: DefaultNotification? ->
    }
  }
  
  protected fun layout(title: String = "", icon: String = "",
                       block: (@VaadinDsl AppLayoutBuilder<TopLarge>).  () -> Unit): TopLarge {
    val appLayoutBuild = AppLayoutBuilder.get(TopLarge::class.java)
    if(title != "")
      appLayoutBuild.withTitle(title)
    if(icon != "")
      appLayoutBuild.withIcon(icon)
    appLayoutBuild.withUpNavigation()
    
    appLayoutBuild.block()
    val appLayout = appLayoutBuild.build()
    init(appLayout)
    appLayout.isUpNavigationEnabled = true
    appLayout.showUpNavigation(true)
    (appLayout as? AbstractLeftAppLayoutBase)?.isMenuVisible = true
    return appLayout
  }
  
  protected fun AppLayoutBuilder<TopLarge>.bar(block: (@VaadinDsl AppBarBuilder).() -> Unit = {}): FlexLayout {
    val appBarBuilder = AppBarBuilder.get()
    appBarBuilder.add(NotificationButton(BELL, notifications))
    
    appBarBuilder.block()
    val appBar = appBarBuilder.build()
    withAppBar(appBar)
    return appBar
  }
  
  protected fun AppLayoutBuilder<TopLarge>.menu(title: String = "", version: String = "",
                                                block: (@VaadinDsl TopAppMenuBuilder).() -> Unit = {}): TopMenuComponent {
    val appMenuBuilder = TopAppMenuBuilder.get()
    // if(title != "")
    //   appMenuBuilder.addToSection(HEADER, TopHeaderItem(title, version, null))
    appMenuBuilder.block()
    val appMenu = appMenuBuilder.build()
    withAppMenu(appMenu)
    return appMenu
  }
  
  protected fun TopAppMenuBuilder.subMenu(title: String, icon: VaadinIcon,
                                          block: (@VaadinDsl TopAppMenuBuilder).  () -> Unit = {}): TopAppMenuBuilder {
    this.block()
    return this
  }
  
  protected fun TopAppMenuBuilder.section(name: String,
                                          block: (@VaadinDsl TopAppMenuBuilder).  () -> Unit = {}): TopAppMenuBuilder {
    this.block()
    return this
  }
  
  protected fun TopAppMenuBuilder.itemMenu(className: KClass<out Component>,
                                           block: (@VaadinDsl TopNavigationItem).() -> Unit = {}): TopNavigationItem {
    val captionAnnotation = className.annotations.firstOrNull {it is Caption} as Caption
    val iconAnnotation = className.annotations.firstOrNull {it is Icon} as Icon
    val menuItemBuilder = TopNavigationItem(captionAnnotation.value, iconAnnotation.value.create(), className.java)
    menuItemBuilder.block()
    add(menuItemBuilder)
    return menuItemBuilder
  }
}


