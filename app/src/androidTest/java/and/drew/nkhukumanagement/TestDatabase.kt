package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.data.Feed
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockDao
import and.drew.nkhukumanagement.data.FlockDatabase
import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.data.Weight
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
class TestDatabase {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @Named("test_database")
    lateinit var database: FlockDatabase
    lateinit var dao: FlockDao

    @Before
    fun init() {
        hiltRule.inject()
        dao = database.flockDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun flock_insertTest() {
        val flock =
            Flock(
                id = 1,
                uniqueId = "ID1",
                batchName = "Batch1",
                breed = "Breed1",
                numberOfChicksPlaced = 100,
                costPerBird = 16.00,
                donorFlock = 1,
                imageResourceId = R.drawable.icon4,
                culls = 2,
                stock = 102,
                mortality = 0,
                datePlaced = LocalDate.now()
            )
        runTest {
            dao.insertFlock(flock)
            val getFlock = dao.retrieveFlock(1).first()
            assertEquals(flock, getFlock)
        }
    }

    @Test
    fun vaccinationInsert() {
        val vaccination = Vaccination(
            id = 1, name = "Gumburro",
            flockUniqueId = "1",
            notes = "",
            date = LocalDate.now()
        )

        val vaccination1 = Vaccination(
            id = 1, name = "Gumburro",
            flockUniqueId = "1",
            notes = "",
            date = LocalDate.now()
        )
        runTest {
            dao.insertVaccination(vaccination)
            dao.insertVaccination(vaccination)

            val getVaccine = dao.retrieveVaccination(1).first()
            assertEquals(vaccination, getVaccine)
        }
    }

    @Test
    fun weightInsert() {
        val weight = Weight(
            id = 1,
            week = "Initial",
            flockUniqueId = "",
            weight = 0.35,
            expectedWeight = 0.40,
            measuredDate = LocalDate.now()
        )

        runTest {
            dao.insertWeight(weight)
            val getWeight = dao.retrieveWeight(1).first()
            assertEquals(weight, getWeight)
        }
    }

    @Test
    fun feedInsert() {
        val feed = Feed(
            id = 1,
            week = "Initial",
            flockUniqueId = "",
            consumed = 100.00,
            actualConsumptionPerBird = 0.2,
            standardConsumption = 110.0,
            standardConsumptionPerBird = 0.3,
            feedingDate = LocalDate.now(),
            name = "Novatek",
            type = "Starter"
        )

        runTest {
            dao.insertFeed(feed)
            val getFeed = dao.retrieveFeed(1).first()
            assertEquals(feed, getFeed)
        }
    }
}