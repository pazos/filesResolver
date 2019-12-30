package org.pazos.fileresolver

import android.os.Build
import android.os.ParcelFileDescriptor
import android.system.Os
import java.io.File
import java.io.IOException

internal object FileUtils {

    /* try to get the absolute path of the file served by local content providers.

       @param pfd - parcelable file descriptor from contentResolver.openFileDescriptor
       @return absolute path to file or null

       NOTE1: works with fileProviders, but not with other/custom content providers.
       NOTE2: The entire workaround is useless in devices/apis where scoped storage is enforced.
       Even when we retrieve the full path to a valid file we don't have read permissions on that path.
     */

    fun getFileDescriptorPath(pfd: ParcelFileDescriptor?): String? {
        return pfd?.let { parcel ->
            try {
                val file = File("/proc/self/fd/" + parcel.fd)
                return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Os.readlink(file.absolutePath)
                } else {
                    file.canonicalPath
                }
            } catch (e: IOException) {
                null
            } catch (e: Exception) {
                null
            }
        }
    }
}