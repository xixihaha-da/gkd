package li.songe.gkd.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.state.ToggleableState

sealed interface Option<T> {
    val value: T
    val label: String
}

interface OptionIcon {
    val icon: ImageVector
}

fun <V, T : Option<V>> Array<T>.findOption(value: V): T {
    return find { it.value == value } ?: first()
}

@Suppress("UNCHECKED_CAST")
val <T> Option<T>.allSubObject: Array<Option<T>>
    get() = when (this) {
        is SortTypeOption -> SortTypeOption.allSubObject
        is UpdateTimeOption -> UpdateTimeOption.allSubObject
        is DarkThemeOption -> DarkThemeOption.allSubObject
        is EnableGroupOption -> EnableGroupOption.allSubObject
        is RuleSortOption -> RuleSortOption.allSubObject
        is UpdateChannelOption -> UpdateChannelOption.allSubObject
    } as Array<Option<T>>

sealed class SortTypeOption(override val value: Int, override val label: String) : Option<Int> {
    data object SortByName : SortTypeOption(0, "按名称")
    data object SortByAppMtime : SortTypeOption(1, "按更新时间")
    data object SortByTriggerTime : SortTypeOption(2, "按触发时间")

    companion object {
        // https://stackoverflow.com/questions/47648689
        val allSubObject by lazy { arrayOf(SortByName, SortByAppMtime, SortByTriggerTime) }
    }
}

sealed class UpdateTimeOption(
    override val value: Long,
    override val label: String
) : Option<Long> {
    data object Pause : UpdateTimeOption(-1, "暂停")
    data object Everyday : UpdateTimeOption(24 * 60 * 60_000, "每天")
    data object Every3Days : UpdateTimeOption(24 * 60 * 60_000 * 3, "每3天")
    data object Every7Days : UpdateTimeOption(24 * 60 * 60_000 * 7, "每7天")

    companion object {
        val allSubObject by lazy { arrayOf(Pause, Everyday, Every3Days, Every7Days) }
    }
}

sealed class DarkThemeOption(
    override val value: Boolean?,
    override val label: String,
    override val icon: ImageVector
) : Option<Boolean?>, OptionIcon {
    data object FollowSystem : DarkThemeOption(null, "自动", Icons.Outlined.AutoMode)
    data object AlwaysEnable : DarkThemeOption(true, "启用", Icons.Outlined.DarkMode)
    data object AlwaysDisable : DarkThemeOption(false, "关闭", Icons.Outlined.LightMode)

    companion object {
        val allSubObject by lazy { arrayOf(FollowSystem, AlwaysEnable, AlwaysDisable) }
    }
}

sealed class EnableGroupOption(
    override val value: Boolean?,
    override val label: String
) : Option<Boolean?> {
    data object FollowSubs : EnableGroupOption(null, "跟随订阅")
    data object AllEnable : EnableGroupOption(true, "全部启用")
    data object AllDisable : EnableGroupOption(false, "全部关闭")

    companion object {
        val allSubObject by lazy { arrayOf(FollowSubs, AllEnable, AllDisable) }
    }
}

fun Option<Boolean?>.toToggleableState() = when (value) {
    true -> ToggleableState.On
    false -> ToggleableState.Off
    null -> ToggleableState.Indeterminate
}

sealed class RuleSortOption(override val value: Int, override val label: String) : Option<Int> {
    data object Default : RuleSortOption(0, "按默认顺序")
    data object ByTime : RuleSortOption(1, "按触发时间")
    data object ByName : RuleSortOption(2, "按名称")

    companion object {
        val allSubObject by lazy { arrayOf(Default, ByTime, ByName) }
    }
}

sealed class UpdateChannelOption(
    override val value: Int,
    override val label: String
) : Option<Int> {
    abstract val url: String

    data object Stable : UpdateChannelOption(0, "稳定版") {
        override val url = "https://registry.npmmirror.com/@gkd-kit/app/latest/files/index.json"
    }

    data object Beta : UpdateChannelOption(1, "测试版") {
        override val url =
            "https://registry.npmmirror.com/@gkd-kit/app-beta/latest/files/index.json"
    }

    companion object {
        val allSubObject by lazy { arrayOf(Stable, Beta) }
    }
}
