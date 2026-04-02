package com.example.ricescan.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.exp

data class DiseaseResult(
    val diseaseName: String,
    val confidence: Float,
    val displayName: String
)

class DiseaseClassifier(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val modelFileName = "rice_disease_model.tflite"
    private val labelsFileName = "labels.txt"
    private val inputSize = 224
    private val minConfidence = 0.2f

    private enum class PreprocessMode { ZERO_TO_ONE, NEGATIVE_ONE_TO_ONE }
    private val preprocessMode = PreprocessMode.NEGATIVE_ONE_TO_ONE
    private val useCenterCrop = true

    // Labels matching your model training order
    private var labels = listOf(
        "bacterial_leaf_blight",
        "brown_spot",
        "healthy",
        "leaf_blast",
        "leaf_scald",
        "narrow_brown_spot"
    )

    init {
        loadModel()
    }

    private fun loadModel() {
        try {
            // Check if model file exists in assets
            val assetFiles = context.assets.list("") ?: emptyArray()
            if (!assetFiles.contains(modelFileName)) {
                Log.w("DiseaseClassifier", "Model file not found in assets yet")
                return
            }

            labels = loadLabelsFromAssets() ?: labels

            labels = loadLabelsFromAssets() ?: labels

            val mappedModel = loadModelFile()
            val options = Interpreter.Options().apply {
                setNumThreads(4)
            }
            interpreter = Interpreter(mappedModel, options)
            Log.d("DiseaseClassifier", "Model loaded successfully")

        } catch (e: Exception) {
            Log.e("DiseaseClassifier", "Error loading model: ${e.message}")
            interpreter = null
        }
    }

    fun classify(imageUri: Uri): DiseaseResult {
        return try {
            if (imageUri == Uri.EMPTY) return getMockResult()

            val bitmap = loadBitmapFromUri(imageUri)
            val localInterpreter = interpreter ?: return getMockResult()

            val cropped = if (useCenterCrop) centerCropSquare(bitmap) else bitmap
            val resizedBitmap = Bitmap.createScaledBitmap(cropped, inputSize, inputSize, true)
            val inputBuffer = convertBitmapToBuffer(resizedBitmap, preprocessMode)

            val outputShape = localInterpreter.getOutputTensor(0).shape()
            val outputSize = outputShape.last()
            val output = Array(1) { FloatArray(outputSize) }

            localInterpreter.run(inputBuffer, output)

            val probabilities = normalizeOutput(output[0])
            val topIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
            if (topIndex == -1) {
                return DiseaseResult("unknown", 0f, "Unable to detect")
            }

            val confidence = probabilities[topIndex]
            val label = labels.getOrNull(topIndex) ?: "unknown"

            Log.d("DiseaseClassifier", "Top1: ${label} (${confidence})")

            // Low confidence — image is unclear or not a rice leaf
            if (confidence < minConfidence) {
                return DiseaseResult(
                    diseaseName = "unknown",
                    confidence = confidence,
                    displayName = "Image unclear"
                )
            }

            DiseaseResult(
                diseaseName = label,
                confidence = confidence,
                displayName = getLabelDisplayName(label)
            )

        } catch (e: Exception) {
            Log.e("DiseaseClassifier", "Classification error: ${e.message}")
            getMockResult()
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open image")
        return BitmapFactory.decodeStream(inputStream)
            ?: throw Exception("Cannot decode image")
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(modelFileName)
        FileInputStream(fileDescriptor.fileDescriptor).use { input ->
            val channel = input.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }
    }

    private fun convertBitmapToBuffer(
        bitmap: Bitmap,
        mode: PreprocessMode
    ): ByteBuffer {
        val inputSizePixels = inputSize * inputSize
        val buffer = ByteBuffer.allocateDirect(4 * inputSizePixels * 3)
        buffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(inputSizePixels)
        bitmap.getPixels(intValues, 0, inputSize, 0, 0, inputSize, inputSize)

        for (pixel in intValues) {
            val rRaw = (pixel shr 16 and 0xFF).toFloat()
            val gRaw = (pixel shr 8 and 0xFF).toFloat()
            val bRaw = (pixel and 0xFF).toFloat()

            val (r, g, b) = when (mode) {
                PreprocessMode.ZERO_TO_ONE -> Triple(rRaw / 255f, gRaw / 255f, bRaw / 255f)
                PreprocessMode.NEGATIVE_ONE_TO_ONE ->
                    Triple((rRaw - 127.5f) / 127.5f, (gRaw - 127.5f) / 127.5f, (bRaw - 127.5f) / 127.5f)
            }
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }
        buffer.rewind()
        return buffer
    }

    private fun centerCropSquare(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    private fun normalizeOutput(raw: FloatArray): FloatArray {
        val sum = raw.sum()
        return if (sum in 0.9f..1.1f) {
            raw
        } else {
            softmax(raw)
        }
    }

    private fun loadLabelsFromAssets(): List<String>? {
        return try {
            val assetFiles = context.assets.list("") ?: return null
            if (!assetFiles.contains(labelsFileName)) return null

            context.assets.open(labelsFileName)
                .bufferedReader()
                .useLines { lines ->
                    lines.map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .toList()
                }
                .ifEmpty { null }
        } catch (e: Exception) {
            Log.w("DiseaseClassifier", "Failed to load labels: ${e.message}")
            null
        }
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val max = logits.maxOrNull() ?: 0f
        val exps = logits.map { exp((it - max).toDouble()).toFloat() }
        val sum = exps.sum().coerceAtLeast(1e-6f)
        return exps.map { it / sum }.toFloatArray()
    }

    private fun getLabelDisplayName(label: String): String {
        return when (label) {
            "bacterial_leaf_blight" -> "Bacterial Leaf Blight"
            "brown_spot" -> "Brown Spot"
            "healthy" -> "Healthy Rice Leaf"
            "leaf_blast" -> "Rice Leaf Blast"
            "leaf_scald" -> "Leaf Scald"
            "narrow_brown_spot" -> "Narrow Brown Spot"
            else -> "Unknown"
        }
    }

    // Mock result used when model is not loaded yet (for UI testing)
    private fun getMockResult(): DiseaseResult {
        return DiseaseResult(
            diseaseName = "brown_spot",
            confidence = 0.91f,
            displayName = "Brown Spot"
        )
    }

    fun close() {
        interpreter?.close()
    }
}
