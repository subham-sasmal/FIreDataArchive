package com.example.firebaseauthenticationpractice

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.Exception

class RegistrationScreen : Fragment() {
    private val TAG = "RegistrationScreen"

    private lateinit var edittxt_email: EditText
    lateinit var edittxt_password: EditText
    lateinit var btn_register: Button
    lateinit var checkedstate: ImageView
    lateinit var textview_sign_in: TextView
    lateinit var edittext_full_name: EditText

    lateinit var auth: FirebaseAuth
    lateinit var googleSignIn: GoogleSignInClient

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

            auth =
                FirebaseAuth.getInstance()               // Saving the instance in auth which of type FirebaseAuth


            /*
                Functionality to change the fragment screen when user clicks on sign in button
             */
            textview_sign_in.setOnClickListener() {
                parentFragmentManager.beginTransaction().apply {
//                    addToBackStack("")
                    replace(R.id.FragmentHolder, LoginScreen()).commit()
                }
            }

            /*
                Functionality to check if the user has accepted the terms and conditions before signing up
             */
            checkedstate.setOnClickListener() {
                if (cntr == 0) {
                    checkedstate.setBackgroundResource(R.drawable.checked_checkbox)
                    cntr++
                } else {
                    checkedstate.setBackgroundResource(R.drawable.unchecked_checkbox)
                    cntr--
                }

            }

            /*
                Functionality to let the user sign-up using email-id and password
             */
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
                                            checkAccountExistence()
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
            /*
                End of functionality of signing up of users using email-id and password
             */

            // -------------------------------------------------------------------------------------

            /*
                Functionality to let the user sign up using Google account
             */
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignIn = GoogleSignIn.getClient(context, gso)

            findViewById<TextView>(R.id.google_sign_up_button).setOnClickListener() {
                if (cntr != 0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            signInGoogle()
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
                } else {
                    checkAccountExistence()
                }
            }
    }

    /*
        Functionality to check if the email used by user to register already exists or not
     */
    private fun checkAccountExistence() {
        FirebaseAuth.getInstance()
            .fetchSignInMethodsForEmail(edittxt_email.text.toString())
            .addOnCompleteListener() { task ->
                if (task.result?.signInMethods?.size == 0) {
                    checkLoggedInState()
                } else {
                    Toast.makeText(context, "Email already in use!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkLoggedInState() {
        if (auth.currentUser != null) {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.FragmentHolder, MainScreen()).commit()
            }
        }
    }
}