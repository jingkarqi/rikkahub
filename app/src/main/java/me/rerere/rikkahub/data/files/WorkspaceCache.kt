package me.rerere.rikkahub.data.files

import java.io.File
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

/** Keep each export at a distinct path so image caches and shared URIs cannot reuse another export. */
internal suspend fun exportWorkspaceCacheFile(
    cacheDir: File,
    fileName: String,
    export: suspend (OutputStream) -> Unit,
): File = withContext(Dispatchers.IO) {
    val dir = File(cacheDir, "workspace_share/${Uuid.random()}")
    check(dir.mkdirs()) { "Cannot create workspace export directory" }
    try {
        File(dir, fileName).also { file ->
            file.outputStream().use { export(it) }
        }
    } catch (error: Throwable) {
        dir.deleteRecursively()
        throw error
    }
}
