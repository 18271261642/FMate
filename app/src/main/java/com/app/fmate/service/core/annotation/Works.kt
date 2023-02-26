package com.app.fmate.service.core.annotation

import com.app.fmate.service.core.IWork
import kotlin.reflect.KClass

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class Works(val value: Array<KClass<out IWork>>)