package com.umg.todohome

data class Task(
    var title: String ?= null,
    var id: String ?= null,
    var description: String ?= null,
    var status: String ?= null,
    var importance: String ?= null,
    var date: String ?= null,
    var name: String ?= null,

)
