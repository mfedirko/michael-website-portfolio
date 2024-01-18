package io.mfedirko.common.infra.back4app

import io.mfedirko.common.util.Dates.TZ_LOCAL
import io.mfedirko.contactme.notification.NotificationPreference
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.LocalDateTime

@SpringBootTest(classes = [
    Back4appTestConfiguration::class,
    Back4appContactNotificationRepository::class,
])
@ActiveProfiles("back4app")
internal class Back4appContactNotificationRepositoryTest {
    @Autowired
    private lateinit var repository: Back4appContactNotificationRepository

    @Test
    fun savePreferencesInitial() {
        val form = NotificationPreference(
            Duration.ofDays(2),
            "to-email@gmail.com",
            "fromemail@email.com"
        )

        repository.updateNotificationPreference(form)

        assertThat(repository.getNotificationPreference())
            .isNotNull
            .usingRecursiveComparison().isEqualTo(form)
    }

    @Test
    fun updatePreferences() {
        var form = NotificationPreference(
            Duration.ofDays(3),
            "to-email@gmail.com",
            "fromemail@email.com"
        )
        repository.updateNotificationPreference(form)
        form = NotificationPreference(
            Duration.ofDays(1),
            "test-email@gmail.com",
            "abc123@gmail.com"
        )
        repository.updateNotificationPreference(form)

        assertThat(repository.getNotificationPreference())
            .isNotNull
            .usingRecursiveComparison().isEqualTo(form)
    }

    @Test
    fun updateLastNotification() {
        repository.updateLastNotificationTime()
        val lastNotificationTime = repository.findLastNotificationTime()

        assertThat(lastNotificationTime)
            .isAfter(LocalDateTime.now(TZ_LOCAL).minusSeconds(40))
            .isBefore(LocalDateTime.now(TZ_LOCAL).plusSeconds(40))
    }
}