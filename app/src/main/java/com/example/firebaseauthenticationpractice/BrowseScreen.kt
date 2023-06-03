package com.example.firebaseauthenticationpractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class BrowseScreen : Fragment() {
    private val dataCollectionRef = Firebase.firestore.collection("data")
    private lateinit var browselist: MutableList<BrowseData>
    private lateinit var recyclerviewdata: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_browse_screen, container, false)

        v.apply {
            recyclerviewdata = findViewById(R.id.recycler_view_browse_data)

            realTimeDatabase()
        }

        return v
    }

    private fun realTimeDatabase() {
        dataCollectionRef.addSnapshotListener { querySnapShot, firebaseFirestoreException ->
            firebaseFirestoreException?.let {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            querySnapShot?.let {
                var retrievedName: String?
                var retrievedAge: Int?
                var retrievedPhone: Long?
                var retrievedAddress: String?

                for (document in it) {
                    val retrievedata = document.toObject<Data>()

                    retrievedName = retrievedata.name
                    retrievedAge = retrievedata.age.toString().toInt()
                    retrievedPhone = retrievedata.contact.toString().toLong()
                    retrievedAddress = retrievedata.address

                    browselist = mutableListOf()
                    browselist.add(BrowseData(retrievedName, retrievedAge, retrievedPhone, retrievedAddress))

                    val adapter = BrowseAdapter(browselist)
                    recyclerviewdata.adapter = adapter
                    recyclerviewdata.layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }
}