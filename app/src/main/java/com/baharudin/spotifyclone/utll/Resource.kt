package com.baharudin.spotifyclone.utll

data class Resource<out T> (
        private var status: Status , val data : T?,val messege : String?
        ) {
    companion object {
        fun <T> succes (data : T?) = Resource(Status.SUCCESS,data,null)

        fun <T> error (messege: String? , data : T?) = Resource(Status.ERROR,data, messege)

        fun <T> loading (data : T?) = Resource(Status.LOADING,data, null)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}