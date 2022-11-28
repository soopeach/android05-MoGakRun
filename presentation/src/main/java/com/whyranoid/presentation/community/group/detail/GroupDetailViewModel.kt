package com.whyranoid.presentation.community.group.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whyranoid.domain.model.FinishNotification
import com.whyranoid.domain.model.GroupNotification
import com.whyranoid.domain.model.StartNotification
import com.whyranoid.domain.usecase.GetGroupInfoUseCase
import com.whyranoid.domain.usecase.GetGroupNotificationsUseCase
import com.whyranoid.presentation.model.GroupInfoUiModel
import com.whyranoid.presentation.model.toGroupInfoUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    getGroupInfoUseCase: GetGroupInfoUseCase,
    getGroupNotificationsUseCase: GetGroupNotificationsUseCase,
    stateHandle: SavedStateHandle
) : ViewModel() {

    private val groupId = stateHandle.get<GroupInfoUiModel>("groupInfo")?.groupId ?: ""

    // TODO : 데이터 스토어에 저장된 Uid와 비교해야함.
    val isLeader =
        stateHandle.get<GroupInfoUiModel>("groupInfo")?.leader?.name == "soopeach"

    private var _groupInfo =
        MutableStateFlow<GroupInfoUiModel>(stateHandle.get<GroupInfoUiModel>("groupInfo")!!)
    val groupInfo: StateFlow<GroupInfoUiModel>
        get() = _groupInfo.asStateFlow()

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow: SharedFlow<Event>
        get() = _eventFlow.asSharedFlow()

    private val startNotification = MutableStateFlow<List<GroupNotification>>(emptyList())
    private val finishNotification = MutableStateFlow<List<GroupNotification>>(emptyList())
    val mergedNotifications = MutableStateFlow<List<GroupNotification>>(emptyList())

    init {

        // TODO : uid를 DataStore에서 가져오도록 변경
        getGroupInfoUseCase("hsjeon", groupId).onEach { groupInfo ->
            println("테스트 $groupInfo")
            _groupInfo.value = groupInfo.toGroupInfoUiModel()
        }.launchIn(viewModelScope)

        // TODO : 그룹 아이디를 프레그먼트에서 받아오도록 변경
        getGroupNotificationsUseCase("수피치 그룹1").onEach { notifications ->

            if (notifications.isNotEmpty() && notifications.first() is StartNotification) {
                startNotification.value = notifications
            } else {
                finishNotification.value = notifications
            }

            mergedNotifications.value =
                (startNotification.value + finishNotification.value)
                    .sortedBy { notification ->
                        when (notification) {
                            is StartNotification -> notification.startedAt
                            is FinishNotification -> notification.runningHistory.startedAt
                        }
                    }
        }.launchIn(viewModelScope)
    }

    fun onRecruitButtonClicked() {
        emitEvent(Event.RecruitButtonClick)
    }

    fun onExitGroupButtonClicked() {
        emitEvent(Event.ExitGroupButtonClick)
    }

    private fun emitEvent(event: Event) {
        when (event) {
            Event.RecruitButtonClick,
            Event.ExitGroupButtonClick -> {
                viewModelScope.launch {
                    _eventFlow.emit(event)
                }
            }
        }
    }
}
