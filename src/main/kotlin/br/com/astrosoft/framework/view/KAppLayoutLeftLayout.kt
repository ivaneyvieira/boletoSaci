package br.com.astrosoft.framework.view

import com.github.appreciated.app.layout.component.appbar.AppBarBuilder
import com.github.appreciated.app.layout.component.applayout.AbstractLeftAppLayoutBase
import com.github.appreciated.app.layout.component.applayout.LeftLayouts.LeftResponsiveHybrid
import com.github.appreciated.app.layout.component.builder.AppLayoutBuilder
import com.github.appreciated.app.layout.component.menu.left.LeftMenu
import com.github.appreciated.app.layout.component.menu.left.LeftMenuComponentWrapper
import com.github.appreciated.app.layout.component.menu.left.LeftSubmenu
import com.github.appreciated.app.layout.component.menu.left.builder.LeftAppMenuBuilder
import com.github.appreciated.app.layout.component.menu.left.builder.LeftSubMenuBuilder
import com.github.appreciated.app.layout.component.menu.left.items.LeftNavigationItem
import com.github.appreciated.app.layout.component.menu.left.items.LeftSectionItem
import com.github.appreciated.app.layout.component.router.AppLayoutRouterLayout
import com.github.mvysny.karibudsl.v10.VaadinDsl
import com.github.mvysny.karibudsl.v10.textAlign
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.component.page.Viewport
import kotlin.reflect.KClass

@Push
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
open class KAppLayoutLeftLayout():
  AppLayoutRouterLayout<LeftResponsiveHybrid?>() {

  protected fun layout(title: String = "", icon: String = "",
                       block: (@VaadinDsl AppLayoutBuilder<LeftResponsiveHybrid>).  () -> Unit): LeftResponsiveHybrid {
    val appLayoutBuild = AppLayoutBuilder.get(LeftResponsiveHybrid::class.java)
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
  
  protected fun AppLayoutBuilder<LeftResponsiveHybrid>.bar(block: (@VaadinDsl AppBarBuilder).() -> Unit = {}): FlexLayout {
    val appBarBuilder = AppBarBuilder.get()
    val versao = Label("Versão 1.0")
    versao.textAlign = "center"
    appBarBuilder.add(versao)
  
    appBarBuilder.block()
    val appBar = appBarBuilder.build()
    withAppBar(appBar)
    return appBar
  }
  
  protected fun AppLayoutBuilder<LeftResponsiveHybrid>.menu(title: String = "", version: String = "",
                                                            block: (@VaadinDsl LeftAppMenuBuilder).() -> Unit = {}): LeftMenu {
    val appMenuBuilder = LeftAppMenuBuilder.get()
    //    if(title != "")
    //      appMenuBuilder.addToSection(HEADER, LeftHeaderItem(title, version, null))
    appMenuBuilder.block()
    val appMenu = appMenuBuilder.build() as LeftMenuComponentWrapper
    withAppMenu(appMenu)
    return appMenu.menu
  }
  
  protected fun LeftAppMenuBuilder.subMenu(title: String, icon: VaadinIcon,
                                           block: (@VaadinDsl LeftSubMenuBuilder).  () -> Unit = {}): LeftSubmenu {
    val subMenuBuilder = LeftSubMenuBuilder.get(title, icon.create())
    subMenuBuilder.block()
    val appSubMenu = subMenuBuilder.build()
    
    add(appSubMenu)
    return appSubMenu.withCloseMenuOnNavigation(false)
  }
  
  protected fun LeftAppMenuBuilder.section(name: String,
                                           block: (@VaadinDsl LeftAppMenuBuilder).  () -> Unit = {}): LeftSectionItem {
    val subMenuBuilder = LeftSectionItem(name)
    add(subMenuBuilder)
    this.block()
    
    return subMenuBuilder
  }
  
  protected fun LeftSubMenuBuilder.itemMenu(className: KClass<out Component>,
                                            block: (@VaadinDsl LeftNavigationItem).() -> Unit = {}): LeftNavigationItem {
    val menuItemBuilder = LeftNavigationItem(className.java)
    menuItemBuilder.block()
    add(menuItemBuilder)
    return menuItemBuilder
  }
  
  protected fun LeftAppMenuBuilder.itemMenu(className: KClass<out Component>,
                                            block: (@VaadinDsl LeftNavigationItem).() -> Unit = {}): LeftNavigationItem {
    val menuItemBuilder = LeftNavigationItem(className.java)
    menuItemBuilder.block()
    add(menuItemBuilder)
    return menuItemBuilder
  }
}


