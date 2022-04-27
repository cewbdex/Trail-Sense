package com.kylecorry.trail_sense.tools.maps.infrastructure.create

import android.content.Context
import android.net.Uri
import com.kylecorry.trail_sense.shared.extensions.onIO
import com.kylecorry.trail_sense.shared.io.Files
import com.kylecorry.trail_sense.tools.maps.domain.Map
import com.kylecorry.trail_sense.tools.maps.infrastructure.IMapRepo

class CreateMapFromImageCommand(private val context: Context, private val repo: IMapRepo) {
    suspend fun execute(uri: Uri): Map? = onIO {
        val defaultName = context.getString(android.R.string.untitled)
        val file = Files.copyToDirectory(context, uri, "maps") ?: return@onIO null

        val map = Map(
            0,
            defaultName,
            Files.getLocalPath(file),
            emptyList(),
            warped = false,
            rotated = false
        )

        val id = repo.addMap(map)
        map.copy(id = id)
    }

}