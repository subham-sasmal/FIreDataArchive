package com.example.firebaseauthenticationpractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginScreen : Fragment() {
    lateinit var edittxt_email: EditText
    lateinit var edittxt_password: EditText
    lateinit var btn_sign_in: Button
    lateinit var textView_sign_up: TextView

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_login_screen, container, false)

        v.apply {
            edittxt_email = findViewById(R.id.et_login_email_id)
            edittxt_password = findViewById(R.id.et_login_password)
            btn_sign_in = findViewById(R.id.btn_login)
            textView_sign_up = findViewById(R.id.tv_login_page_sign_up)

            auth = FirebaseAuth.getInstance()

            textView_sign_up.setOnClickListener() {
                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.FragmentHolder, RegistrationScreen()).commit()
                }
            }

            btn_sign_in.setOnClickListener() {
                val getemail_id = edittxt_email.text.toString()
                val getpassword = edittxt_password.text.toString()

                if (getemail_id.isEmpty()) {
                    Toast.makeText(context, "Invalid email-id entered!", Toast.LENGTH_SHORT).show()
                } else if (getpassword.isEmpty()) {
                    Toast.makeText(context, "Invalid password entered!", Toast.LENGTH_SHORT).show()
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            auth.signInWithEmailAndPassword(getemail_id, getpassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                       checkLoggedInState()
                                    } else {
                                        Toast.makeText(context,"Wrong Email-id or Password", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        return v
    }

    private fun checkLoggedInState() {
        if (auth.currentUser == null) {
            Toast.makeText(context, "You are not logged-in", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "You are logged-in", Toast.LENGTH_SHORT).show()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.FragmentHolder, MainScreen()).commit()
            }
        }
    }
}