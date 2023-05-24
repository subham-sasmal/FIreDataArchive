package com.example.firebaseauthenticationpractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainScreen : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var btn_signout: Button

    private lateinit var googleSignIn : GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main_screen, container, false)

        v.apply {
            btn_signout = findViewById(R.id.btn_sign_out)

            auth = FirebaseAuth.getInstance()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignIn = GoogleSignIn.getClient(context, gso)

            btn_signout.setOnClickListener() {
                auth.signOut()

                googleSignIn.signOut().addOnCompleteListener {
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.FragmentHolder, LoginScreen()).commit()
                    }
                }
//
//                parentFragmentManager.beginTransaction().apply {
//                    replace(R.id.FragmentHolder, LoginScreen()).commit()
//                }
            }
        }

        return v
    }
}