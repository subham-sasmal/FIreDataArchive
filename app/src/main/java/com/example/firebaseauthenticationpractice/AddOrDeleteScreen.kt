package com.example.firebaseauthenticationpractice

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddOrDeleteScreen : Fragment() {

    private lateinit var et_name: EditText
    private lateinit var et_age: EditText
    private lateinit var et_phone: EditText
    private lateinit var et_address: EditText
    private lateinit var btn_save_data: Button
    private lateinit var btn_retrieve_data: Button
    private lateinit var btn_update_data: Button
    private lateinit var btn_delete_data: Button
    private lateinit var text_name: TextView
    private lateinit var text_age: TextView
    private lateinit var text_phone: TextView
    private lateinit var text_address: TextView


    private val dataCollectionReference = Firebase.firestore.collection("data")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_add_or_delete_screen, container, false)

        v.apply {
            et_name = findViewById(R.id.et_enter_name)
            et_age = findViewById(R.id.et_enter_age)
            et_phone = findViewById(R.id.et_contact_details)
            et_address = findViewById(R.id.et_address_details)
            btn_save_data = findViewById(R.id.btn_save_data)
            btn_retrieve_data = findViewById(R.id.btn_retrieve_data)
            btn_update_data = findViewById(R.id.btn_update_data)
            btn_delete_data = findViewById(R.id.btn_delete_data)
            text_name = findViewById(R.id.tv_retrieved_name)
            text_age = findViewById(R.id.tv_retrieved_age)
            text_phone = findViewById(R.id.tv_retrieved_phone)
            text_address = findViewById(R.id.tv_retrieved_address)


            btn_save_data.setOnClickListener() {
                val personData = currentRegisteredUsers()

                if (personData != null)
                    saveData(personData)
                else {
                    Toast.makeText(context, "Empty fields detected!", Toast.LENGTH_SHORT).show()
                }
            }

            btn_retrieve_data.setOnClickListener() {
                retrieveData()
            }

            btn_update_data.setOnClickListener() {
                updateData()
            }

            btn_delete_data.setOnClickListener() {
                val personData = currentRegisteredUsers()

                if (personData != null)
                    deleteDetails(personData)
                else {
                    Toast.makeText(context, "Empty fields detected!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return v
    }

    private fun saveData(dataobj: Data) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dataCollectionReference.add(dataobj).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Data saved!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun retrieveData() {
        var retrievedName: String = ""
        var retrievedAge: Int? = -1
        var retrievedPhone: Long? = -1
        var retrievedAddress: String = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = dataCollectionReference
                    .whereGreaterThan("age", 20)
                    .whereLessThan("age", 30)
                    .get().await()

                for (document in querySnapshot.documents) {
                    val retrievedata = document.toObject<Data>()

                    retrievedName = retrievedata?.name.toString()
                    retrievedAge = retrievedata?.age.toString().toInt()
                    retrievedPhone = retrievedata?.contact.toString().toLong()
                    retrievedAddress = retrievedata?.address.toString()
                }

                withContext(Dispatchers.Main) {
                    if (retrievedName != "") {
                        text_name.text = "Name: $retrievedName"
                        text_age.text = "Age: $retrievedAge"
                        text_phone.text = "Phone No.: $retrievedPhone"
                        text_address.text = "Address: $retrievedAddress"
                    }

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateData() {
        parentFragmentManager.beginTransaction().apply {
            addToBackStack("")
            replace(R.id.FragmentHolder_screens, UpdateDataScreen()).commit()
        }
    }


    private fun deleteData() {

    }


    fun currentRegisteredUsers(): Data? {
        var datareturnobj: Data? = null

        if (et_name.text.toString() == "")
            Toast.makeText(context, "Name field empty!", Toast.LENGTH_SHORT).show()
        else if (et_age.text.toString() == "")
            Toast.makeText(context, "Age field empty!", Toast.LENGTH_SHORT).show()
        else if (et_phone.text.toString() == "")
            Toast.makeText(context, "Contact details field empty!", Toast.LENGTH_SHORT).show()
        else if (et_address.text.toString() == "")
            Toast.makeText(context, "Address field empty!", Toast.LENGTH_SHORT).show()
        else {
            val name = et_name.text.toString()
            val age = et_age.text.toString().toInt()
            val phone = et_phone.text.toString().toLong()
            val address = et_address.text.toString()

            datareturnobj = Data(name, age, phone, address)
        }

        return datareturnobj
    }




    private fun deleteDetails (dataObject: Data?) {
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
                        // This will delete the whole document for which we get the ID
                        dataCollectionReference.document(document.id).delete().await()

                        // This will only delete the attribute that we mention to delete for the document
                        dataCollectionReference.document(document.id).update(mapOf("name" to FieldValue.delete()))

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Data deleted successfully", Toast.LENGTH_SHORT).show()
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