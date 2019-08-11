package com.teamttdvlp.memolang.view.Activity.mockmodel

data class Flashcard (var id : Int, var text : String, var translation : String, var using : String) {
    constructor() : this (0, "", "", "")
}