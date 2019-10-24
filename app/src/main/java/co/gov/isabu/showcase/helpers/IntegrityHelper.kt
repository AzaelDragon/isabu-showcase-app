package co.gov.isabu.showcase.helpers

import androidx.appcompat.app.AppCompatActivity
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream

/**
 * Helper that provides checksum verification utilities.
 */

class IntegrityHelper {

    companion object {

        /**
         * Verifies the integrity of the provided object, using the Apache Commons: Codec SHA-512 hashing
         * implementation capsuled in a Thread-Safe environment and requesting the local storage and
         * map checksum to prove a correct match.
         *
         * @param jsonObject the object to analyze.
         * @return true if the both the target and generated checksum match, false if they don't.
         */

        fun verifyIntegrity(activity: AppCompatActivity, jsonObject: JSONObject) : Boolean {

            val fileName = jsonObject.getString("name")
            val storageLocation = StorageHelper.buildStoragePath(activity, fileName)
            val storageFile = File(storageLocation)
            val baseChecksum = jsonObject.getString("checksum")

            val checksumCheck = ThreadLocal<Boolean>()
            checksumCheck.set(verifyChecksum(storageFile, baseChecksum))

            return checksumCheck.get()!!

        }

        /**
         * Provides a thread-unsafe implementation of checksum verification and local hashing with an
         * encoded HEX string that can be used for comparison purposes.
         *
         * @param storedFile the local file to generate a hash from.
         * @param checksum the reference checksum to compare from.
         * @return true if the both the target and generated checksum match, false if they don't.
         */

        private fun verifyChecksum(storedFile: File, checksum: String) : Boolean {

            val dataStream = FileInputStream(storedFile)
            val rawDigest = DigestUtils.sha512(dataStream)
            val digest = String(Hex.encodeHex(rawDigest))
            dataStream.close()

            return digest == checksum

        }

    }

}