package and.drew.nkhukumanagement.DI

import and.drew.nkhukumanagement.BaseFlockApplication
import dagger.hilt.android.testing.CustomTestApplication

@CustomTestApplication(BaseFlockApplication::class)
interface CustomHiltTestApplication {
}