package com.example.demo.model

import com.example.demo.model.enums.JobStatus
import com.example.demo.model.enums.JobType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Job(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        var type: Enum<JobType>,
        var status: Enum<JobStatus>,
        var file: String = ""
)