package com.example.demo.service

import com.example.demo.model.GeologicalClass
import com.example.demo.model.Section
import com.example.demo.repo.GeoRepo
import com.example.demo.repo.JobRepo
import com.example.demo.repo.SectionRepo
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


@Service
class XlsService(val sectionRepo: SectionRepo, val jobRepo: JobRepo, val geoRepo: GeoRepo) {

    @Async
    fun import(file: MultipartFile) {
            val sectionList: MutableList<Section> = mutableListOf()
            val tmpDir: Path = Files.createTempDirectory("")
            val tmpFile: File = tmpDir.resolve(file.originalFilename).toFile()

            file.transferTo(tmpFile)

            val inputStream = FileInputStream(tmpFile)
            val xlWb = WorkbookFactory.create(inputStream)
            val sheet = xlWb.getSheetAt(0)

            for (i in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(i)
                val geoList: MutableList<GeologicalClass> = mutableListOf()
                var j = 1
                while (j < row.lastCellNum - 1) {
                    val name = row.getCell(j).toString()
                    val code = row.getCell(++j).toString()
                    val geo = GeologicalClass(name = name, code = code)
                    geoList.add(geo)
                    j++
                }
                val section = Section(name = row.getCell(0).toString(), geologicalClasses = geoList)
                sectionList.add(section)
            }
            sectionRepo.saveAll(sectionList)
            xlWb.close()
    }

    @Async
    fun export(localFileName: String) {
            val xlWb = XSSFWorkbook()
            val xlWs = xlWb.createSheet()

            //header
            val titlesRow = xlWs.createRow(0)
            titlesRow.createCell(0).setCellValue("Section name")

            val maxPair = geoRepo.findAll().groupingBy { it.sec_id }.eachCount().maxBy { it.value }?.value
            val maxcells = maxPair!! * 2

            var k = 1  // (i/2)+1
            for (i in 1 until maxcells step 2) {
                titlesRow.createCell(i).setCellValue("Class $k name")
                titlesRow.createCell(i + 1).setCellValue("Class $k code")
                k++
            }

            var bodyRow = xlWs.lastRowNum + 1

            val sections = sectionRepo.findAll()

            for (s in sections) {
                val row = xlWs.createRow(bodyRow++)
                row.createCell(0).setCellValue(s.name)
                val geo = s.geologicalClasses
                for (g in geo) {
                    row.createCell(row.lastCellNum.toInt()).setCellValue(g.name)
                    row.createCell(row.lastCellNum.toInt()).setCellValue(g.code)
                }
            }

            val tmpDir: Path
            tmpDir = if (!Files.exists(Paths.get("C:/xls/export"))) {
                Files.createDirectory(Paths.get("C:/xls/export"))
            } else Paths.get("C:/xls/export")

            val tmpFile: File = tmpDir.resolve(localFileName).toFile()

            val outputStream = FileOutputStream(tmpFile)
            xlWb.write(outputStream)
            xlWb.close()

            println("tmpDir: $tmpDir")
            println("localFileName: $localFileName")
            println("tmpFile: $tmpFile")
    }
}