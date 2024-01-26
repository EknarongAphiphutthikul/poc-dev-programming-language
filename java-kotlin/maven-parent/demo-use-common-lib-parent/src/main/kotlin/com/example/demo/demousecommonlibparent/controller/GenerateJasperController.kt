package com.example.demo.demousecommonlibparent.controller

import com.example.commonlib.poccommonlib.jasper.JasperUtils
import com.example.demo.demousecommonlibparent.constants.EndpointConstant
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(EndpointConstant.GEN_JASPER)
class GenerateJasperController {

    val jasperUtils = JasperUtils()

    // controller method GET
    // return content-type: application/pdf
    // return content-disposition: attachment; filename=report.pdf
    @GetMapping(EndpointConstant.GEN_JASPER_PDF)
    fun exportPdf(): ResponseEntity<ByteArray?>? {
        val pdfStream = jasperUtils.genPdfToByteArray("poc-jasper-path.jasper", null, null)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=poc-report.pdf")
        headers.contentLength = pdfStream.size().toLong()
        return ResponseEntity<ByteArray?>(pdfStream.toByteArray(), headers, HttpStatus.OK)
    }
}