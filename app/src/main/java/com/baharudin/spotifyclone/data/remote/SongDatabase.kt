package com.baharudin.spotifyclone.data.remote

import com.baharudin.spotifyclone.data.entity.Song
import com.baharudin.spotifyclone.utll.Constans.Companion.COLLECTION_SONG
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SongDatabase {
    private var firestore = FirebaseFirestore.getInstance()
    private var songCollection = firestore.collection(COLLECTION_SONG)

    suspend fun getAllSong() : List<Song>{
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        }catch (e : Exception){
            emptyList()
        }
    }
}