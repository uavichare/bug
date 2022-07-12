package com.example.buglibrary.utils

import okhttp3.internal.and
import java.util.*

/**
 * Utils class to help with conversion of Bytes to Hex.
 */
class ConversionUtils {
    companion object {
        private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

        /**
         * Converts byte[] to an iBeacon [UUID].
         * From http://stackoverflow.com/a/9855338.
         *
         * @param bytes Byte[] to convert
         * @return UUID
         */
        fun bytesToUuid(bytes: ByteArray): UUID {
            val hexChars = CharArray(bytes.size * 2)
            for (j in bytes.indices) {
                val v: Int = bytes[j] and 0xFF
                hexChars[j * 2] = HEX_ARRAY[v ushr 4]
                hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
            }
            val hex = String(hexChars)
            return UUID.fromString(
                hex.substring(0, 8) + "-" +
                        hex.substring(8, 12) + "-" +
                        hex.substring(12, 16) + "-" +
                        hex.substring(16, 20) + "-" +
                        hex.substring(20, 32)
            )
        }

        /**
         * Converts a [UUID] to a byte[]. This is used to create a [android.bluetooth.le.ScanFilter].
         * From http://stackoverflow.com/questions/29664316/bluetooth-le-scan-filter-not-working.
         *
         * @param uuid UUID to convert to a byte[]
         * @return byte[]
         */
        fun UuidToByteArray(uuid: UUID): ByteArray {
            val hex = uuid.toString().replace("-", "")
            val length = hex.length
            val result = ByteArray(length / 2)
            var i = 0
            while (i < length) {
                result[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(
                    hex[i + 1],
                    16
                )).toByte()
                i += 2
            }
            return result
        }

        /**
         * Convert major or minor to hex byte[]. This is used to create a [android.bluetooth.le.ScanFilter].
         *
         * @param value major or minor to convert to byte[]
         * @return byte[]
         */
        fun integerToByteArray(value: Int): ByteArray {
            val result = ByteArray(2)
            result[0] = (value / 256).toByte()
            result[1] = (value % 256).toByte()
            return result
        }

        /**
         * Convert major and minor byte array to integer.
         *
         * @param byteArray that contains major and minor byte
         * @return integer value for major and minor
         */
        fun byteArrayToInteger(byteArray: ByteArray): Int {
            return (byteArray[0] and 0xff) * 0x100 + (byteArray[1] and 0xff)
        }
    }

}