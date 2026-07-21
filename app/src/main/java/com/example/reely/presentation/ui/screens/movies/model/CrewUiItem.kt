package com.example.reely.presentation.ui.screens.movies

import com.example.reely.domain.model.CrewMember

data class CrewUiItem(
    val personId: Int,
    val name: String,
    val profilePath: String?,
    val roles: List<String>,
    val priority: Int
)

fun List<CrewMember>.toCrewUiItems(): List<CrewUiItem> {
    return groupBy { member -> member.id }
        .map { (_, credits) ->
            val jobs = credits
                .mapNotNull { member -> member.job }
                .distinct()

            CrewUiItem(
                personId = credits.first().id,
                name = credits.first().name,
                profilePath = credits
                    .firstNotNullOfOrNull { member ->
                        member.profilePath
                    },
                roles = jobs.map(::crewJobText),
                priority = credits.minOfOrNull(::crewPriority)
                    ?: Int.MAX_VALUE
            )
        }
        .sortedWith(
            compareBy<CrewUiItem> { item -> item.priority }
                .thenBy { item -> item.name }
        )
}

private fun crewPriority(member: CrewMember): Int {
    return when (member.job) {
        "Director" -> 0

        "Screenplay" -> 10
        "Writer" -> 11
        "Story" -> 12
        "Novel" -> 13

        "Producer" -> 20
        "Executive Producer" -> 21
        "Co-Producer" -> 22

        "Director of Photography" -> 30
        "Original Music Composer" -> 40
        "Editor" -> 50
        "Casting" -> 60
        "Production Design" -> 70
        "Costume Design" -> 80

        else -> when (member.department) {
            "Directing" -> 100
            "Writing" -> 110
            "Production" -> 120
            "Camera" -> 130
            "Sound" -> 140
            "Editing" -> 150
            "Art" -> 160
            "Costume & Make-Up" -> 170
            "Visual Effects" -> 180
            else -> 1_000
        }
    }
}

private fun crewJobText(job: String): String {
    return when (job) {
        "Director" -> "Reżyser"
        "Screenplay" -> "Scenariusz"
        "Writer" -> "Scenarzysta"
        "Story" -> "Historia"
        "Novel" -> "Autor powieści"

        "Producer" -> "Producent"
        "Executive Producer" -> "Producent wykonawczy"
        "Co-Producer" -> "Koproducent"

        "Director of Photography" -> "Zdjęcia"
        "Original Music Composer" -> "Muzyka"
        "Editor" -> "Montaż"
        "Casting" -> "Casting"
        "Production Design" -> "Scenografia"
        "Costume Design" -> "Kostiumy"

        else -> job
    }
}