package com.teamttdvlp.memolang.view.customview

object AppThemeManager {
    var theme: Theme = Theme.DEFAULT_BLUE
    var isInNightMode: Boolean = false
}

enum class Theme(var code: Int) {
    DEFAULT_BLUE(0),
    RED(1),
    GREEN(2),
    YELLOW(3),
    PINK(4),
    DARK_VIOLET(5)
}