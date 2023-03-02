package com.example.flow_playground.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    val countDownFlow = flow<Int> {
        val startingValue = 5
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    init {
        collectCountDownFlow()
    }

    private fun collectCountDownFlow() {
        viewModelScope.launch {
            countDownFlow.collect {
                println("countdown $it")
            }
        }
    }

    //this will ommit previous value if new value is availaible
    //it emits only latest values
    fun collectCountDownFlowLatest() {
        viewModelScope.launch {
            countDownFlow.collectLatest {
                delay(1500)
                println("countdown latest $it")
            }
        }
    }
}