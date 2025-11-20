package me.rerere.ai.provider.providers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rerere.ai.provider.ImageGenerationParams
import me.rerere.ai.provider.Model
import me.rerere.ai.provider.Provider
import me.rerere.ai.provider.ProviderSetting
import me.rerere.ai.provider.TextGenerationParams
import me.rerere.ai.ui.ImageAspectRatio
import me.rerere.ai.ui.ImageGenerationItem
import me.rerere.ai.ui.ImageGenerationResult
import me.rerere.ai.ui.MessageChunk
import me.rerere.ai.ui.UIMessage
import me.rerere.ai.util.KeyRoulette
import me.rerere.ai.util.configureClientWithProxy
import me.rerere.ai.util.json
import me.rerere.ai.util.mergeCustomBody
import me.rerere.ai.util.toHeaders
import me.rerere.common.http.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal

class PPIOProvider(
    private val client: OkHttpClient
) : Provider<ProviderSetting.PPIO> {
    private val keyRoulette = KeyRoulette.default()

    override suspend fun listModels(providerSetting: ProviderSetting.PPIO): List<Model> = 
        withContext(Dispatchers.IO) {
            // PPIO Seedream 4.0模型列表
            listOf(
                Model(
                    modelId = "seedream-4.0",
                    displayName = "Seedream 4.0",
                    type = me.rerere.ai.provider.ModelType.IMAGE,
                    inputModalities = setOf(me.rerere.ai.provider.Modality.TEXT),
                    outputModalities = setOf(me.rerere.ai.provider.Modality.IMAGE),
                    abilities = emptySet()
                )
            )
        }

    override suspend fun getBalance(providerSetting: ProviderSetting.PPIO): String = 
        "PPIO余额查询功能待实现"

    override suspend fun generateText(
        providerSetting: ProviderSetting.PPIO,
        messages: List<UIMessage>,
        params: TextGenerationParams
    ): MessageChunk {
        throw UnsupportedOperationException("PPIO提供商目前仅支持图像生成")
    }

    override suspend fun streamText(
        providerSetting: ProviderSetting.PPIO,
        messages: List<UIMessage>,
        params: TextGenerationParams
    ): Flow<MessageChunk> {
        throw UnsupportedOperationException("PPIO提供商目前仅支持图像生成")
    }

    override suspend fun generateImage(
        providerSetting: ProviderSetting,
        params: ImageGenerationParams
    ): ImageGenerationResult = withContext(Dispatchers.IO) {
        require(providerSetting is ProviderSetting.PPIO) {
            "Expected PPIO provider setting"
        }

        val key = keyRoulette.next(providerSetting.apiKey)

        // 映射宽高比到PPIO尺寸
        val size = when (params.aspectRatio) {
            ImageAspectRatio.SQUARE -> "2048x2048"    // 1:1
            ImageAspectRatio.LANDSCAPE -> "2560x1440" // 16:9
            ImageAspectRatio.PORTRAIT -> "1440x2560"  // 9:16
        }

        // 构建请求体
        val requestBody = json.encodeToString(
            buildJsonObject {
                put("prompt", params.prompt)
                put("size", size)
                put("sequential_image_generation", "disabled")  // 禁用批量生成，仅生成单张
                put("max_images", params.numOfImages.coerceIn(1, 15))
                put("watermark", false)  // 不添加水印
            }.mergeCustomBody(params.customBody)
        )

        // 构建HTTP请求
        val request = Request.Builder()
            .url("${providerSetting.baseUrl}/seedream-4.0")
            .headers(params.customHeaders.toHeaders())
            .addHeader("Authorization", "Bearer $key")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .build()

        // 执行请求
        val response = client.configureClientWithProxy(providerSetting.proxy).newCall(request).await()
        
        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: "Unknown error"
            throw IllegalStateException("Failed to generate image: ${response.code} - $errorBody")
        }

        // 解析响应
        val bodyStr = response.body?.string() ?: ""
        val bodyJson = json.parseToJsonElement(bodyStr).jsonObject
        val images = bodyJson["images"]?.jsonArray ?: error("No images in response")

        // 转换为ImageGenerationItem列表
        val items = images.map { imageJson ->
            val base64Data = imageJson.jsonPrimitive.content
            ImageGenerationItem(
                data = base64Data,
                mimeType = "image/png"
            )
        }

        ImageGenerationResult(items = items)
    }
}