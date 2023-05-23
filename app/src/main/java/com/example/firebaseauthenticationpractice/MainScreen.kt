package com.example.firebaseauthenticationpractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainScreen : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var btn_signout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main_screen, container, false)

        v.apply {
            btn_signout = findViewById(R.id.btn_sign_out)

            auth = FirebaseAuth.getInstance()

            btn_signout.setOnClickListener() {
                auth.signOut()

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.FragmentHolder, LoginScreen()).commit()
                }
            }
        }

        return v
    }
}