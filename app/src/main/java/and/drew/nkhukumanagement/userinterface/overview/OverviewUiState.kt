package and.drew.nkhukumanagement.userinterface.overview

import and.drew.nkhukumanagement.data.AccountsSummary
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class OverviewUiState(val accountsList: SnapshotStateList<AccountsSummary> = mutableStateListOf()) {
}