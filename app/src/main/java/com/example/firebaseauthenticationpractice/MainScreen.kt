package com.example.firebaseauthenticationpractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainScreen : Fragment() {
    private lateinit var auth: FirebaseAuth

    private lateinit var btn_browse_data: TextView
    private lateinit var tv_browse_data: TextView
    private lateinit var btn_add_delete_data: TextView
    private lateinit var tv_add_delete_data: TextView
    private lateinit var btn_signout: TextView

    private lateinit var googleSignIn : GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_main_screen, container, false)

        v.apply {
            btn_browse_data = findViewById(R.id.btn_browse)
            tv_browse_data = findViewById(R.id.tv_browse_data)
            btn_add_delete_data = findViewById(R.id.btn_add_delete)
            tv_add_delete_data = findViewById(R.id.tv_add_delete_data)
            btn_signout = findViewById(R.id.btn_sign_out)

            parentFragmentManager.beginTransaction().apply {
                replace(R.id.FragmentHolder_screens, BrowseScreen()).commit()
            }

            btn_browse_data.setBackgroundResource(R.drawable.bg_button_browse_or_add_delete)
            tv_browse_data.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.cream_white
                ))

            btn_browse_data.setOnClickListener() {
                btn_browse_data.setBackgroundResource(R.drawable.bg_button_browse_or_add_delete)
                tv_browse_data.setTextColor(
                    ContextCompat.getColor(
                    context,
                    R.color.cream_white
                ))

                btn_add_delete_data.setBackgroundResource(R.drawable.bg_google_sign_up_button)
                tv_add_delete_data.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.violet_light
                    ))

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.FragmentHolder_screens, BrowseScreen()).commit()
                }
            }

            btn_add_delete_data.setOnClickListener() {
                btn_add_delete_data.setBackgroundResource(R.drawable.bg_button_browse_or_add_delete)
                tv_add_delete_data.setTextColor(
                    ContextCompat.getColor(
                    context,
                    R.color.cream_white
                ))

                btn_browse_data.setBackgroundResource(R.drawable.bg_google_sign_up_button)
                tv_browse_data.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.violet_light
                    ))

                parentFragmentManager.beginTransaction().apply {
                    replace(R.id.FragmentHolder_screens, AddOrDeleteScreen()).commit()
                }
            }

            auth = FirebaseAuth.getInstance()

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignIn = GoogleSignIn.getClient(context, gso)

            /*
                ------------------------------------------------------------------------------------
                Functionality to log out the user upon clicking on the sign out button
             */
            btn_signout.setOnClickListener() {
                auth.signOut()

                googleSignIn.signOut().addOnCompleteListener {
                    parentFragmentManager.beginTransaction().apply {
                        replace(R.id.FragmentHolder, LoginScreen()).commit()
                    }
                }
            }
            /*
                End of sign out functionality
                ------------------------------------------------------------------------------------
             */
        }

        return v
    }
}