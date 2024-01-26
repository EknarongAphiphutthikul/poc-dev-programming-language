package com.example.commonlib.poccommonlib.jasper

import com.example.commonlib.poccommonlib.utils.getLogger
import com.lowagie.text.pdf.PdfWriter
import net.sf.jasperreports.engine.JRDataSource
import net.sf.jasperreports.engine.JREmptyDataSource
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.data.JsonDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import net.sf.jasperreports.export.SimplePdfExporterConfiguration
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.sql.Connection

class JasperUtils {

    private val JASPER_PATH = "jaspers/"
    private val IMAGE_PATH = "images/"
    private val JASPER_ROOT_PATH_PARAM_NAME = "jasper_root_path"
    private val IMAGE_ROOT_PATH_PARAM_NAME = "image_root_path"
    private val JASPER_JAR_PATH = "BOOT-INF/classes/jaspers/"
    private val IMAGE_JAR_PATH = "BOOT-INF/classes/images/"
    private val JAR_EXTENSION = ".jar"
    private val JASPER_EXTENSION = ".jasper"
    private val SLASH = "/"
    private var ROOT_PATH = ""

    private val logger = getLogger { }

    constructor() {
        logger.info("JasperUtils new instance")
        this.javaClass.classLoader.getResource("")?.path.run {
            logger.info("path resource : %s".format(this))
            var jarPath = this?.let {
                var lastIndex = it.indexOf(JAR_EXTENSION)
                lastIndex = when (lastIndex) {
                    -1 -> it.length
                    else -> lastIndex + 4
                }
               it.substring(it.indexOf(SLASH), lastIndex)
            }

            logger.info("jar path : %s".format(jarPath))
            jarPath?.let {
                ROOT_PATH = it
                if (it.endsWith(JAR_EXTENSION)) {
                    ROOT_PATH = it.substring(0, it.lastIndexOf(SLASH) + 1)
                    val rootPathJasper = ROOT_PATH+JASPER_PATH
                    val rootPathImage = ROOT_PATH+IMAGE_PATH
                    createDirectory(rootPathJasper)
                    createDirectory(rootPathImage)
                    val jar = java.util.jar.JarFile(it)
                    val entries = jar.entries() //gives ALL entries in jar
                    while (entries.hasMoreElements()) {
                        val name = entries.nextElement().name
                        if (name.startsWith(JASPER_JAR_PATH) && name.endsWith(JASPER_EXTENSION)) {
                            copyFileResource(rootPathJasper+name.substring(name.lastIndexOf(SLASH) + 1), name)
                        }
                        if (name.startsWith(IMAGE_JAR_PATH) && !name.endsWith(SLASH)) {
                            copyFileResource(rootPathImage+name.substring(name.lastIndexOf(SLASH) + 1), name)
                        }
                    }
                }
            } ?: throw RuntimeException("jar path is not exists")
        } ?: throw RuntimeException("resources path is not exists")
    }

    private fun copyFileResource(outputFullPath: String, inputFullPath: String) {
        val f = java.io.File(outputFullPath)
        if (f.exists()) {
            logger.info("$outputFullPath is exists")
        } else {
            logger.info("Copy file $inputFullPath to $outputFullPath")
            val isStream = javaClass.classLoader.getResourceAsStream(inputFullPath)
            isStream.use { input ->
                java.io.FileOutputStream(f).use { output ->
                    input?.copyTo(output)
                }
            }
        }
    }

    private fun createDirectory(path: String) {
        logger.info("createDirectory path: %s".format(path))
        val directory = java.io.File(path)
        if (!directory.exists()) {
            directory.mkdirs()
        }
    }

    fun genPdfToByteArray(jasperTemplateName: String, param: MutableMap<String, Any>?, password : String?) : ByteArrayOutputStream {
        return ByteArrayOutputStream().also {
            genPdfWithJRDataSource(jasperTemplateName, param, null, password, SimpleOutputStreamExporterOutput(it))
        }
    }

    fun genPdfToFile(jasperTemplateName: String, param: MutableMap<String, Any>?, password : String?, outputFileName : String) {
        genPdfWithJRDataSource(jasperTemplateName, param, null, password, SimpleOutputStreamExporterOutput(outputFileName))
    }

    fun genPdfWithConnectionToByteArray(jasperTemplateName: String, param: MutableMap<String, Any>?, conn : Connection, password : String?) : ByteArrayOutputStream {
        return ByteArrayOutputStream().also {
            genPdfWithConnection(jasperTemplateName, param, conn, password, SimpleOutputStreamExporterOutput(it))
        }
    }

    fun genPdfWithConnectionToFile(jasperTemplateName: String, param: MutableMap<String, Any>?, conn : Connection, password : String?, outputFileName : String) {
        genPdfWithConnection(jasperTemplateName, param, conn, password, SimpleOutputStreamExporterOutput(outputFileName))
    }

    fun genPdfWithJsonContentToByteArray(jasperTemplateName: String, param: MutableMap<String, Any>?, jsonStr : String, password : String?) : ByteArrayOutputStream {
        return ByteArrayOutputStream().also {
            genPdfWithJRDataSource(jasperTemplateName, param, getJsonDatasource(jsonStr), password, SimpleOutputStreamExporterOutput(it))
        }
    }

    fun genPdfWithJsonContentToFile(jasperTemplateName: String, param: MutableMap<String, Any>?, jsonStr : String, password : String?, outputFileName : String) {
        genPdfWithJRDataSource(jasperTemplateName, param, getJsonDatasource(jsonStr), password, SimpleOutputStreamExporterOutput(outputFileName))
    }

    fun genPdfWithBeanCollectionToByteArray(jasperTemplateName: String, param: MutableMap<String, Any>?, listBean : List<Any>, password : String?) : ByteArrayOutputStream {
        return ByteArrayOutputStream().also {
            genPdfWithJRDataSource(jasperTemplateName, param, getJRBeanCollectionDataSource(listBean), password, SimpleOutputStreamExporterOutput(it))
        }
    }

    fun genPdfWithBeanCollectionToFile(jasperTemplateName: String, param: MutableMap<String, Any>?, listBean : List<Any>, password : String?, outputFileName : String) {
        genPdfWithJRDataSource(jasperTemplateName, param, getJRBeanCollectionDataSource(listBean), password, SimpleOutputStreamExporterOutput(outputFileName))
    }

    private fun genPdfWithConnection(
        jasperTemplateName: String,
        param: MutableMap<String, Any>?,
        connection: Connection?,
        password: String?,
        exporterOutput : SimpleOutputStreamExporterOutput
    ) {
        getJasperTemplate(jasperTemplateName).use {
            exportPdfReport(
                JasperFillManager.fillReport(
                    it,
                    addJasperAndImageRootPathToParam(param),
                    connection
                ),
                getPdfExporterConfiguration(password),
                exporterOutput
            )
        }
    }

    private fun genPdfWithJRDataSource(
        jasperTemplateName: String,
        param: MutableMap<String, Any>?,
        jrDatasource: JRDataSource?,
        password: String?,
        exporterOutput : SimpleOutputStreamExporterOutput
    ) {
        getJasperTemplate(jasperTemplateName).use {
            exportPdfReport(
                JasperFillManager.fillReport(
                    it,
                    addJasperAndImageRootPathToParam(param),
                    jrDatasource ?: JREmptyDataSource()
                ),
                getPdfExporterConfiguration(password),
                exporterOutput
            )
        }
    }

    private fun addJasperAndImageRootPathToParam(param: MutableMap<String, Any>?): Map<String, Any> {
        return (param ?: mutableMapOf()).apply {
            this[JASPER_ROOT_PATH_PARAM_NAME] = "%s%s".format(ROOT_PATH, JASPER_PATH)
            this[IMAGE_ROOT_PATH_PARAM_NAME] = "%s%s".format(ROOT_PATH, IMAGE_PATH)
        }
    }

    private fun getJsonDatasource(jsonStr: String): JsonDataSource {
        return JsonDataSource(ByteArrayInputStream(jsonStr.toByteArray(Charsets.UTF_8)))
    }

    private fun getJRBeanCollectionDataSource(dataList: List<Any>): JRBeanCollectionDataSource {
        return JRBeanCollectionDataSource(dataList)
    }

    private fun exportPdfReport(
        jasperPrint: JasperPrint,
        pdfExportConfig: SimplePdfExporterConfiguration,
        exporterOutput: SimpleOutputStreamExporterOutput
    ) {
        JRPdfExporter().apply {
            this.setExporterInput(SimpleExporterInput(jasperPrint))
            this.exporterOutput = exporterOutput
            this.setConfiguration(pdfExportConfig)
        }.run {
            this.exportReport()
        }
    }

    private fun getPdfExporterConfiguration(password: String?): SimplePdfExporterConfiguration {
        return SimplePdfExporterConfiguration().apply {
            this.permissions = PdfWriter.ALLOW_COPY or PdfWriter.ALLOW_PRINTING
            password?.let {
                this.isEncrypted = true
                this.is128BitKey = true
                this.ownerPassword = it
                this.userPassword = it
            }
        }
    }

    private fun getJasperTemplate(jasperTemplateName: String): InputStream {
        return java.io.File("${ROOT_PATH}${JASPER_PATH}$jasperTemplateName").inputStream()
    }

    private fun getResourceRootPath(): String {
        return this.javaClass.classLoader.getResource("")?.path ?: throw RuntimeException("resources path is not exists")
    }
}