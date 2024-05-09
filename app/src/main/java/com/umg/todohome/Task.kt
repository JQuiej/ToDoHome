package com.umg.todohome

data class Task(
    var title: String ?= null,
    var description: String ?= null,
    var status: String ?= null,
    var importance: String ?= null,
)
