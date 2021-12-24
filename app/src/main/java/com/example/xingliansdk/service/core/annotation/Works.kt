package com.example.xingliansdk.service.core.annotation

import com.example.xingliansdk.service.core.IWork
import kotlin.reflect.KClass

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class Works(val value: Array<KClass<out IWork>>)