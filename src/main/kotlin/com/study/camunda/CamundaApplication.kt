package com.study.camunda

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@EnableProcessApplication
@SpringBootApplication
class CamundaApplication

fun main(args: Array<String>) {
	runApplication<CamundaApplication>(*args)
}
