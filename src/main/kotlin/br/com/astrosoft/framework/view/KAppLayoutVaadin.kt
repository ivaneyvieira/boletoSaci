package br.com.astrosoft.framework.view

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.AppLayout.Section.DRAWER
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.tabs.Tabs.Orientation.VERTICAL

class KAppLayoutVaadin : AppLayout() {
  init {
    primarySection = DRAWER
    val img = Image("https://i.imgur.com/GPpnszs.png", "Vaadin Logo")
    img.setHeight("44px")
    addToNavbar(DrawerToggle(), img)
    val tabs = Tabs(Tab("Home"), Tab("About"))
    tabs.orientation = VERTICAL
    addToDrawer(tabs)
  }
  
}