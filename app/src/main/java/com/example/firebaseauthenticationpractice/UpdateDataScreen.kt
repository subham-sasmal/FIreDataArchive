package com.example.firebaseauthenticationpractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UpdateDataScreen : Fragment() {
    private val dataCollectionReference = Firebase.firestore.collection("data")

    private lateinit var updateName: EditText
    private lateinit var updateAge: EditText
    private lateinit var updateContact: EditText
    private lateinit var updateAddress: EditText

    private lateinit var btnUpdateDetails: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_update_data_screen, container, false)

        v.apply {
            updateName = findViewById(R.id.et_update_name)
            updateAge = findViewById(R.id.et_update_age)
            updateContact = findViewById(R.id.et_update_details)
            updateAddress = findViewById(R.id.et_update_address)
            btnUpdateDetails = findViewById(R.id.update_data)

            val addOrDeleteScreenObj = AddOrDeleteScreen()

            btnUpdateDetails.setOnClickListener() {
                updateDetails(addOrDeleteScreenObj.currentRegisteredUsers(), newDataToUpdate(updateName, updateAge, updateContact, updateAddress))
            }
        }

        return v
    }

    private fun newDataToUpdate(updatedName: EditText, updatedAge: EditText, updateContactDetails: EditText, updatedAddress: EditText): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        if (updatedName.text.toString().isNotEmpty()) {
            map["name"] = updatedName.text.toString()
        }
        if (updatedAge.text.toString().isNotEmpty()) {
            map["age"] = updatedAge.text.toString().toInt()
        }
        if (updateContactDetails.text.toString().isNotEmpty()) {
            map["contact"] = updateContactDetails.text.toString().toLong()
        }
        if (updatedAddress.text.toString().isNotEmpty()) {
            map["address"] = updatedAddress.text.toString()
        }

        return map
    }


    private fun updateDetails (dataObject: Data?, newMap: Map<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            val dataQuery = dataCollectionReference
                .whereEqualTo("name", dataObject?.name)
                .whereEqualTo("age", dataObject?.age)
                .whereEqualTo("contact", dataObject?.contact)
                .whereEqualTo("address", dataObject?.address)
                .get()
                .await()

            if (dataQuery.documents.isNotEmpty()) {
                for (document in dataQuery.documents) {
                    try {
                        dataCollectionReference.document(document.id).set(
                            newMap,
                            SetOptions.merge()
                        )

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Data updated successfully", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No match for the entered data!!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}