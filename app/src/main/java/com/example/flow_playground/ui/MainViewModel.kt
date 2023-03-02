package com.example.flow_playground.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

    private fun collectCountDownFlowOperators() {
        viewModelScope.launch {
            countDownFlow.filter { it % 2 == 0 }
                .map { it * it }
                .collect {
                    println("countdown operator $it")
                }
        }
    }

    private fun collectCountDownFlowReduce() {
        viewModelScope.launch {
            val reducedValue=countDownFlow.reduce { ac, value ->
                ac + value
            }
            println("countdown reduce $reducedValue")
        }
    }

    private fun collectCountDownFlowFold() {
        viewModelScope.launch {
            val reducedValue=countDownFlow.fold(100) { ac, value ->
                ac + value
            }
            println("countdown fold $reducedValue")
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