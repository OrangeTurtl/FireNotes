package com.stepanf.firenotes.model

class Note {
    var title: String? = null
    var content: String? = null
    constructor()
    constructor(title:String, content:String) {
        this.title = title
        this.content = content
    }
}