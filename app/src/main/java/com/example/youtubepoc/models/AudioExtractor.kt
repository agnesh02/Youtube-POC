package com.example.youtubepoc.models

import android.annotation.SuppressLint
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class AudioExtractor {
    @SuppressLint("WrongConstant")
     fun extractAudioFromVideo(inputVideoPath: String, outputAudioPath: String) {
        val mediaExtractor = MediaExtractor()
        val mediaMuxer = MediaMuxer(outputAudioPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        try {
            val inputFile = File(inputVideoPath)
            val outputFile = File(outputAudioPath)

            val inputStream = FileInputStream(inputFile)
            val outputStream = FileOutputStream(outputFile)

            mediaExtractor.setDataSource(inputStream.fd)

            val audioTrackIndex = selectTrack(mediaExtractor, "audio/")
            val audioFormat = mediaExtractor.getTrackFormat(audioTrackIndex)

            val audioTrack = mediaMuxer.addTrack(audioFormat)
            mediaMuxer.start()

            val byteBuffer = ByteBuffer.allocate(1024 * 1024)
            val bufferInfo = MediaCodec.BufferInfo()

            mediaExtractor.selectTrack(audioTrackIndex)

            while (true) {
                val sampleSize = mediaExtractor.readSampleData(byteBuffer, 0)

                if (sampleSize < 0) {
                    break
                }

                bufferInfo.offset = 0
                bufferInfo.size = sampleSize
                bufferInfo.presentationTimeUs = mediaExtractor.sampleTime
                bufferInfo.flags = mediaExtractor.sampleFlags

                mediaMuxer.writeSampleData(audioTrack, byteBuffer, bufferInfo)

                mediaExtractor.advance()
            }

            mediaMuxer.stop()
            mediaMuxer.release()
            mediaExtractor.release()

            inputStream.close()
            outputStream.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun selectTrack(extractor: MediaExtractor, mimeType: String): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith(mimeType)) {
                return i
            }
        }
        return -1
    }
}