package com.whyranoid.presentation.community

import com.whyranoid.presentation.model.GroupInfoUiModel

sealed class Event {
    data class GroupItemClick(val groupInfo: GroupInfoUiModel) : Event()
    data class GroupJoin(val isSuccess: Boolean = true) : Event()
}
