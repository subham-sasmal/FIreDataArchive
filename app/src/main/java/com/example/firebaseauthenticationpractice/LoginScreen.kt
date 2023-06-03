package com.example.firebaseauthenticationpractice

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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
    private lateinit var googleSignIn: GoogleSignInClient

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

            /*
                Functionality to let the user sign in using email-id and password
             */
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
                                        Toast.makeText(
                                            context,
                                            "Wrong Email-id or Password",
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
                }
            }
            // -------------------------------------------------------------------------------------

            /*
                Functionality to let the user sign in using google account
             */
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignIn = GoogleSignIn.getClient(context, gso)

            findViewById<TextView>(R.id.google_sign_in_button).setOnClickListener() {
                signInGoogle()
            }
            //--------------------------------------------------------------------------------------
        }

        return v
    }

    private fun signInGoogle() {
        val signInIntent = googleSignIn.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }

        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credentials)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    checkLoggedInState()
                }
            }
    }

    private fun checkLoggedInState() {
        if (auth.currentUser == null) {
            Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show()
        } else {
           // Toast.makeText(context, "You are logged-in", Toast.LENGTH_SHORT).show()
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.FragmentHolder, MainScreen()).commit()
            }
        }
    }
}