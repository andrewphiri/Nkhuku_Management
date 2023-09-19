package com.example.nkhukumanagement

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.nkhukumanagement.data.AccountsSummary

class OverviewUiState(val accountsList: SnapshotStateList<AccountsSummary> = mutableStateListOf()) {
}