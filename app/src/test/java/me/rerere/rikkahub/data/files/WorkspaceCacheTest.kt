package me.rerere.rikkahub.data.files

import java.io.IOException
import java.nio.file.Files
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.fail
import org.junit.Test

class WorkspaceCacheTest {
    @Test
    fun `same name exports preserve both contents at different paths`() = runBlocking {
        val cacheDir = Files.createTempDirectory("workspace-cache-test").toFile()
        try {
            val first = exportWorkspaceCacheFile(cacheDir, "image.png") { it.write("first".toByteArray()) }
            val second = exportWorkspaceCacheFile(cacheDir, "image.png") { it.write("second".toByteArray()) }

            assertNotEquals(first.absolutePath, second.absolutePath)
            assertEquals("image.png", first.name)
            assertEquals("image.png", second.name)
            assertEquals("first", first.readText())
            assertEquals("second", second.readText())
        } finally {
            cacheDir.deleteRecursively()
        }
    }

    @Test
    fun `failed export removes only its own temporary directory`() = runBlocking {
        val cacheDir = Files.createTempDirectory("workspace-cache-test").toFile()
        try {
            val first = exportWorkspaceCacheFile(cacheDir, "image.png") { it.write("first".toByteArray()) }
            val error = IOException("Export failed")
            try {
                exportWorkspaceCacheFile(cacheDir, "image.png") {
                    it.write("partial".toByteArray())
                    throw error
                }
                fail("Expected export failure")
            } catch (actual: IOException) {
                assertEquals(error.message, actual.message)
            }

            assertEquals("first", first.readText())
            assertEquals(listOf(first.parentFile), cacheDir.resolve("workspace_share").listFiles()!!.toList())
        } finally {
            cacheDir.deleteRecursively()
        }
    }
}
