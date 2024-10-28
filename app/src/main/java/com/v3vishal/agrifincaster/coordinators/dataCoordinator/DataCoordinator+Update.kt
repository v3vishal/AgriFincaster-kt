// ktlint-disable filename
package com.v3vishal.agrifincaster.coordinators.dataCoordinator

import com.v3vishal.agrifincaster.coordinators.dataCoordinator.DataCoordinator
import com.v3vishal.agrifincaster.coordinators.dataCoordinator.setSampleBooleanDataStore
import com.v3vishal.agrifincaster.coordinators.dataCoordinator.setSampleIntDataStore
import com.v3vishal.agrifincaster.coordinators.dataCoordinator.setSampleStringDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// MARK: DataStore Update Functionality
fun DataCoordinator.updateSampleString(value: String) {
    // Update Value
    this.sampleStringPreferenceVariable = value
    // Save to System
    GlobalScope.launch(Dispatchers.Default) {
        // Update DataStore
        setSampleStringDataStore(value)
        // OPTIONAL - Send Broadcast
        // Not included in this tutorial - consult the ReadMe to learn how to setup notifications to alert your system.
    }
}

fun DataCoordinator.updateSampleInt(value: Int) {
    // Update Value
    this.sampleIntPreferenceVariable = value
    // Save to System
    GlobalScope.launch(Dispatchers.Default) {
        // Update DataStore
        setSampleIntDataStore(value)
        // OPTIONAL - Send Broadcast
        // Not included in this tutorial - consult the ReadMe to learn how to setup notifications to alert your system.
    }
}

fun DataCoordinator.updateSampleBoolean(value: Boolean) {
    // Update Value
    this.sampleBooleanPreferenceVariable = value
    // Save to System
    GlobalScope.launch(Dispatchers.Default) {
        // Update DataStore
        setSampleBooleanDataStore(value)
        // OPTIONAL - Send Broadcast
        // Not included in this tutorial - consult the ReadMe to learn how to setup notifications to alert your system.
    }
}
