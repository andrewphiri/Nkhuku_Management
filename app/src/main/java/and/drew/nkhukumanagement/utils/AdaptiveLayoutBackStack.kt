package and.drew.nkhukumanagement.utils

/**
 * Determines current screen to show for the list details screen for Expanded Screens.
 * Manual navigation for Expanded screens
 */
enum class FlockDetailsCurrentScreen {
    DETAILS_SCREEN, FLOCK_HEALTH_SCREEN, WEIGHT_SCREEN, FEED_SCREEN,
    VACCINATION_SCREEN, EDIT_FLOCK_SCREEN, EGG_INVENTORY_SCREEN, EDIT_EGGS_SCREEN
}

enum class AccountDetailsCurrentScreen {
    TRANSACTIONS_SCREEN, ADD_EXPENSE_SCREEN, ADD_INCOME_SCREEN
}

enum class OverViewDetailsCurrentScreen {
    ACCOUNTS_OVERVIEW_SCREEN, FLOCK_OVERVIEW_SCREEN
}

enum class TipsAndDetailsCurrentScreen {
    ARTICLES_LIST_SCREEN, SINGLE_ARTICLE_SCREEN
}