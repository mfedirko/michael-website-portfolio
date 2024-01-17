package io.mfedirko.common.infra.back4app

import io.mfedirko.common.infra.back4app.Back4appQueryUtil.OrderDir.DESC
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.between
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.equals
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.orderBy
import io.mfedirko.common.infra.back4app.Back4appQueryUtil.where
import io.mfedirko.common.util.Dates.toUtcEndOfYear
import io.mfedirko.common.util.Dates.toUtcStartOfYear
import io.mfedirko.learning.CreateLessonForm
import io.mfedirko.learning.LearningRepository
import io.mfedirko.learning.Lesson
import io.mfedirko.learning.UpdateLessonForm
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cache.annotation.CacheConfig
import org.springframework.context.annotation.Profile
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Repository
import org.springframework.web.client.RestTemplate
import java.time.Year


@Repository
@Profile("back4app")
@CacheConfig(cacheNames = ["lessons"])
class Back4appLearningRepository(
    @Qualifier("back4appTemplate") restTemplateBuilder: RestTemplateBuilder
) : LearningRepository {
    private val restTemplate: RestTemplate
    init {
        restTemplate = restTemplateBuilder.build()
    }

    override fun findLessons(year: Year): List<Lesson> {
        val resp = restTemplate.getForEntity(
            "/classes/Lesson?where={where}&order={order}",
            LessonResults::class.java,
            where(
                between("createdAt", year.toUtcStartOfYear(), year.toUtcEndOfYear())
            ),
            orderBy("updatedAt" to DESC)
        )
        return resp.body?.results?.map {
            it.toLesson()
        } ?: listOf()
    }

    override fun getLesson(id: Any): Lesson? {
        val resp = restTemplate.getForEntity(
            "/classes/Lesson?where={where}",
            LessonResults::class.java,
            where(
                equals("objectId", id)
            )
        )
        return resp.body?.results?.firstOrNull()?.toLesson()
    }

    override fun createLesson(lesson: CreateLessonForm): Any {
        val resp = restTemplate.postForEntity("/classes/Lesson", Back4appLessonForm(lesson, getCurrentUser()), Map::class.java)
        return resp.body["objectId"]!!
    }

    private fun getCurrentUser() = SecurityContextHolder.getContext().authentication.name

    override fun updateLesson(lesson: UpdateLessonForm, id: Any) {
        val existingLesson = getLesson(id)
            ?: return
        restTemplate.put("/classes/Lesson/{id}", Back4appLessonForm(lesson, existingLesson), existingLesson.id)
    }

    override fun deleteLesson(id: Any) {
        val objectId = getLesson(id)?.id
            ?: return
        restTemplate.delete("/classes/Lesson/{id}", objectId)
    }

    internal class LessonResults: Back4appResults<Back4appLessonResult>()
}