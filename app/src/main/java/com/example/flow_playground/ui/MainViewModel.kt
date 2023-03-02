package com.example.flow_playground.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<Int>(replay = 5)
    val sharedFlow = _sharedFlow.asSharedFlow()

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
        viewModelScope.launch {
          exampleCombine()
        }
    }

    private fun sharedFlowExample() {
        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000L)
                println("FIRST FLOW: The received number is $it")
            }
        }
        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)
                println("SECOND FLOW: The received number is $it")
            }
        }
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
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
            val reducedValue = countDownFlow.reduce { ac, value ->
                ac + value
            }
            println("countdown reduce $reducedValue")
        }
    }

    private fun collectCountDownFlowFold() {
        viewModelScope.launch {
            val reducedValue = countDownFlow.fold(100) { ac, value ->
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

    @OptIn(FlowPreview::class)
    private fun collectFlow() {
        viewModelScope.launch {
            val flow1 = flow {
                emit(1)
                delay(500L)
                emit(2)
            }
            flow1.flatMapConcat {
                flow {
                    emit(it + 1)
                    delay(500L)
                    emit(it + 2)
                }
            }.collect{
                println("flatmapconcat values $it")
            }
        }
    }

    private suspend fun exampleOfMerge(){
        val ints: Flow<Int> = flowOf(1, 2, 3)
        val doubles: Flow<Double> = flowOf(0.1, 0.2, 0.3)

        val together: Flow<Number> = merge(ints, doubles)
        print("merge "+together.toList())
        // [1, 0.1, 0.2, 0.3, 2, 3]
        // or [1, 0.1, 0.2, 0.3, 2, 3]
        // or [0.1, 1, 2, 3, 0.2, 0.3]
        // or any other combination
    }

    //it closes when the first flow closes
    private suspend fun exampleOfZip(){
            val flow1 = flowOf("A", "B", "C")

            val flow2 = flowOf(1, 2, 3, 4)
            flow1.zip(flow2) { f1, f2 -> "${f1}_${f2}" }
                .collect { println("zip "+it) }

        // A_1
        // B_2
        // C_3

    }

    suspend fun exampleCombine() {
        val flow1 = flowOf("A", "B", "C")
        val flow2 = flowOf(1, 2, 3, 4)
        flow1.combine(flow2) { f1, f2 -> "${f1}_${f2}" }
            .collect { println("combine "+it) }

        //A_1
        // B_1
        // C_1
        // C_2
        // C_3
        // C_4
    }

}