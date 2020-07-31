package com.example.demo.controllers

import com.example.demo.model.enums.JobStatus
import com.example.demo.model.enums.JobType
import com.example.demo.model.Job
import com.example.demo.repo.JobRepo
import com.example.demo.service.XlsService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@RestController
class XLSController(private val xlsService: XlsService, val jobRepo: JobRepo) {
    @PostMapping("/import")
    fun upload(@RequestParam("file") file: MultipartFile): Map<String, String> {
        val job = Job(type = JobType.IMPORT, status = JobStatus.INPROGRESS)
        try {
            xlsService.import(file)
        } catch (e: Exception) {
            job.status = JobStatus.ERROR
        }
        job.status = JobStatus.DONE
        val save = jobRepo.save(job)
        return mapOf("jobId" to save.id.toString())
    }

    @GetMapping("/import/{id}")
    fun findJobById(@PathVariable("id") id: Int): Map<String, String> {
        val job = jobRepo.findJobById(id.toLong())
        return if (job?.type == JobType.IMPORT)
            mapOf("jobStatus" to "${job.status}")
        else mapOf("jobStatus" to "такого ид не существует")
    }

    @GetMapping("/export")
    fun createXLS(): Map<String, String> {
        val job = Job(type = JobType.EXPORT, status = JobStatus.INPROGRESS)
        val localFileName: String = "xls_" + Date().time.toString() + ".xlsx"
        try {
            xlsService.export(localFileName)
        } catch (e: Exception) {
            job.status = JobStatus.ERROR
        }
        job.status = JobStatus.DONE
        job.file = localFileName
        val save = jobRepo.save(job)
        return mapOf("jobId" to save.id.toString())
    }

    @GetMapping("/export/{id}")
    fun checkStatusImport(@PathVariable("id") id: Int): Map<String, String> {
        val job = jobRepo.findJobById(id.toLong())
        return if (job != null && job.type == JobType.EXPORT)
            mapOf("jobStatus" to "${job.status}")
        else mapOf("jobStatus" to "такого ид не существует")
    }

    @GetMapping("/export/{id}/file")
    fun exportFile(@PathVariable("id") id: Int): ResponseEntity<*> {
        var respEntity: ResponseEntity<*>? = null
        val fileName = jobRepo.findJobById(id.toLong())?.file
        val path = "C:\\xls\\export\\"
        //val r = Paths.get("$path$fileName")
        val result = File("$path$fileName")
        respEntity = if (result.exists()) {
            val read = Files.readAllBytes(result.toPath())
            val responseHeaders = HttpHeaders()
            responseHeaders.add("content-disposition", "attachment; filename=$fileName")
            ResponseEntity<Any?>(read, responseHeaders, HttpStatus.OK)
        } else {
            ResponseEntity<Any?>("файл не найден", HttpStatus.OK)
        }
        return respEntity
    }
}