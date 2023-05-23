package com.example.firebaseauthenticationpractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class RegistrationScreen : Fragment() {
    private lateinit var edittxt_email: EditText
    lateinit var edittxt_password: EditText
    lateinit var btn_register: Button
    lateinit var checkedstate: ImageView
    lateinit var textview_sign_in: TextView
    lateinit var edittext_full_name: EditText

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_registration_screen, container, false)

        v.apply {
            edittxt_email = findViewById(R.id.et_email_id)
            edittxt_password = findViewById(R.id.et_password)
            btn_register = findViewById(R.id.btn_create_account)
            checkedstate = findViewById(R.id.iv_check_box)
            textview_sign_in = findViewById(R.id.tv_sign_in)
            edittext_full_name = findViewById(R.id.et_full_name)

            var cntr = 0

            auth = FirebaseAuth.getInstance()

            textview_sign_in.setOnClickListener() {
                parentFragmentManager.beginTransaction().apply {
//                    addToBackStack("")
                    replace(R.id.FragmentHolder, LoginScreen()).commit()
                }
            }

            checkedstate.setOnClickListener() {
                if (cntr == 0) {
                    checkedstate.setBackgroundResource(R.drawable.checked_checkbox)
                    cntr++
                } else {
                    checkedstate.setBackgroundResource(R.drawable.unchecked_checkbox)
                    cntr--
                }

            }

            btn_register.setOnClickListener() {
                val getemail_id = edittxt_email.text.toString()
                val getpassword = edittxt_password.text.toString()

                if (edittext_full_name.text.toString().isEmpty())
                    Toast.makeText(context, "Name field empty!", Toast.LENGTH_SHORT).show()
                if (getemail_id.isEmpty()) {
                    Toast.makeText(context, "Invalid email-id entered!", Toast.LENGTH_SHORT).show()
                } else if (getpassword.isEmpty()) {
                    Toast.makeText(context, "Invalid password entered!", Toast.LENGTH_SHORT).show()
                } else {
                    if (cntr != 0) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                auth.createUserWithEmailAndPassword(getemail_id, getpassword)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            checkLoggedInState()
                                        } else {
                                                Toast.makeText(
                                                    context,
                                                    "Authentication failed.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                        }
                                    }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else
                        Toast.makeText(
                            context,
                            "Please accept the Terms & Conditions",
                            Toast.LENGTH_SHORT
                        ).show()
                }
            }
        }

        return v
    }

    private fun checkLoggedInState() {
        if (auth.currentUser != null) {
            Toast.makeText(context, "Successfully registered", Toast.LENGTH_SHORT).show()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.FragmentHolder, MainScreen()).commit()
            }
        } else {
            Toast.makeText(context, "Account already exists", Toast.LENGTH_SHORT).show()
        }
    }
}