package com.example.demo.demousecommonlibparent.controller

import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@RestController
class FileDownloadController {

    @GetMapping("/download/{fileName:.+}")
    @Throws(IOException::class)
    fun downloadFile(@PathVariable fileName: String): ResponseEntity<Resource> {
        // Load file as Resource
        val filePath = Paths.get("/local-path/$fileName")
        val resource = ByteArrayResource(Files.readAllBytes(filePath))

        val contentType: String
        try {
            contentType = determineContentType(fileName)
        } catch (ex: java.lang.IllegalArgumentException) {
            throw RuntimeException("Unsupported file type: $fileName", ex)
        }

        // Set Content-Disposition header for download
        val headers: HttpHeaders = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$fileName")

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(Files.size(filePath))
                .contentType(MediaType.parseMediaType(contentType))
                .lastModified(Files.getLastModifiedTime(filePath).toMillis())
                .body(resource)
    }

    private fun determineContentType(fileName: String): String {
        // You can implement your logic to determine content type based on file extension here
        return if (fileName.endsWith(".pdf")) {
            "application/pdf"
        } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
            "application/msword"
        } else if (fileName.endsWith(".txt")) {
            "text/plain"
        } else if (fileName.endsWith(".sql")) {
            "application/sql"
        } else {
            throw IllegalArgumentException("Unsupported file type")
        }
    }
}