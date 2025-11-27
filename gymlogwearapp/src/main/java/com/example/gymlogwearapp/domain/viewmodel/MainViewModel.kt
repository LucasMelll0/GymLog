package com.example.gymlogwearapp.domain.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DataTypeAvailability
import androidx.health.services.client.data.DeltaDataType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymlogwearapp.presentation.MeasureMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(context: Context) : ViewModel() {
    private val healthServicesClient = HealthServices.getClient(context)
    private val measureClient = healthServicesClient.measureClient
    val hr: MutableStateFlow<Double> = MutableStateFlow(0.0)
    val availability: MutableState<DataTypeAvailability> =
        mutableStateOf(DataTypeAvailability.UNKNOWN)
    val enabled: MutableStateFlow<Boolean> = MutableStateFlow(false)


    init {
        viewModelScope.launch {
            enabled.collect {
                if (it) {
                    checkHeartRate()
                        .takeWhile { enabled.value }
                        .collect { measureMessage ->
                        when (measureMessage) {
                            is MeasureMessage.MeasureData -> {
                                hr.value = measureMessage.data.last().value
                                Log.d("measureMessage", "heart rate: ${hr.value}")
                            }

                            is MeasureMessage.MeasureAvailability -> {
                                availability.value = measureMessage.availability
                            }
                        }
                    }
                }
            }
        }
    }

    fun toggleEnabled() {
        enabled.value = !enabled.value
        if (!enabled.value) {
            availability.value = DataTypeAvailability.UNKNOWN
        }
    }

    private fun checkHeartRate() = callbackFlow<MeasureMessage> {
        val capabilities = measureClient.getCapabilitiesAsync().await()
        val supportsHealthRate = DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure
        if (supportsHealthRate) {
            val heartRateCallback = object : MeasureCallback {
                override fun onAvailabilityChanged(
                    dataType: DeltaDataType<*, *>,
                    availability: Availability
                ) {
                    if (availability is DataTypeAvailability) {
                        trySendBlocking(MeasureMessage.MeasureAvailability(availability))
                    }
                }

                override fun onDataReceived(data: DataPointContainer) {
                    val heartRate = data.getData(DataType.HEART_RATE_BPM)
                    trySendBlocking(MeasureMessage.MeasureData(heartRate))
                }

            }
            measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, heartRateCallback)

            awaitClose {
                runBlocking {
                    measureClient.unregisterMeasureCallbackAsync(
                        DataType.HEART_RATE_BPM,
                        heartRateCallback
                    ).await()
                }
            }
        }
    }
}