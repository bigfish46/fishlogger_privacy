package com.fishercreative.fishlogger.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.fishercreative.fishlogger.data.models.Catch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.core.content.FileProvider

data class ExportResult(
    val filePath: String,
    val uri: Uri
)

object CsvExporter {
    private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val filenameDateFormatter = DateTimeFormatter.ofPattern("MMddyyyy")
    private val filenameTimeFormatter = DateTimeFormatter.ofPattern("HHmm")

    fun exportCatches(context: Context, catches: List<Catch>): ExportResult {
        val now = LocalDateTime.now()
        val dateStr = now.format(filenameDateFormatter)
        val timeStr = now.format(filenameTimeFormatter)
        val filename = "FishLogger_${dateStr}_$timeStr.csv"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, use MediaStore
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, filename)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw IllegalStateException("Failed to create new MediaStore record.")

            resolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).buffered().use { writer ->
                    // Write header
                    writer.write(getHeaderRow())
                    writer.newLine()

                    // Write data rows
                    catches.forEach { catch ->
                        writer.write(getCatchRow(catch))
                        writer.newLine()
                    }
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            return ExportResult(
                filePath = "Downloads/$filename",
                uri = uri
            )
        } else {
            // For Android 9 and below, use direct file access
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, filename)

            BufferedWriter(FileWriter(file)).use { writer ->
                // Write header
                writer.write(getHeaderRow())
                writer.newLine()

                // Write data rows
                catches.forEach { catch ->
                    writer.write(getCatchRow(catch))
                    writer.newLine()
                }
            }

            // Get content URI using FileProvider
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            return ExportResult(
                filePath = file.absolutePath,
                uri = uri
            )
        }
    }

    private fun getHeaderRow(): String {
        return listOf(
            "Date",
            "Time",
            "Species",
            "Length (inches)",
            "Weight (lbs)",
            "Weight (oz)",
            "Air Temperature (°F)",
            "Cloud Cover",
            "Latitude",
            "Longitude",
            "Location",
            "Water Body",
            "Bait Type",
            "Bait Color",
            "Water Turbidity",
            "Water Temperature (°F)",
            "Water Depth (ft)",
            "Fishing Depth (ft)",
            "Retrieval Method"
        ).joinToString(",") { escapeField(it) }
    }

    private fun getCatchRow(catch: Catch): String {
        return listOf(
            catch.date.format(dateFormatter),
            catch.time.format(timeFormatter),
            catch.species,
            catch.lengthInches.toString(),
            catch.weightPounds.toString(),
            catch.weightOunces.toString(),
            catch.temperature.toString(),
            catch.cloudCover.name,
            catch.latitude?.toString() ?: "",
            catch.longitude?.toString() ?: "",
            catch.nearestCity,
            catch.waterBody,
            catch.baitType,
            catch.baitColor,
            catch.waterTurbidity.name,
            catch.waterTemperature.toString(),
            catch.waterDepth.toString(),
            catch.fishingDepth.toString(),
            catch.retrievalMethod.name
        ).joinToString(",") { escapeField(it) }
    }

    private fun escapeField(field: String): String {
        // If the field contains commas, quotes, or newlines, wrap it in quotes and escape any quotes
        return if (field.contains(Regex("[\",\n\r]"))) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }
} 