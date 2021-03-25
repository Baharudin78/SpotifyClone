package com.baharudin.spotifyclone.utll

open class Event <out T>(private val data : T) {

    var hasBeenHandled = false
            private set

    fun getContentIsNotHandled() : T? {
        return if (hasBeenHandled){
            null
        }else {
            hasBeenHandled = true
            data
        }
    }
    fun peekContent() = data
}