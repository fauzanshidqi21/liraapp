package com.lira.liraapp

class ModelCategory {

    //variables
    var id:String = ""
    var category:String = ""
    var uid:String = ""
    var timestamp:Long = 0

    //empty constructor
    constructor()

    //parameterized constructor
    constructor(id: String, category: String, uid: String, timestamp: Long) {
        this.id = id
        this.category = category
        this.uid = uid
        this.timestamp = timestamp
    }
}