package com.privatevault

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var securePrefs: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSecureStorage()

        if (passwordExists()) {
            showMailbox()
        } else {
            showPasswordSetup()
        }
    }

    private fun setupSecureStorage() {

        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        securePrefs = EncryptedSharedPreferences.create(
            this,
            "private_vault_secure",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun passwordExists(): Boolean {
        return securePrefs.contains("password_hash")
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))

        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    private fun savePassword(password: String) {
        securePrefs.edit()
            .putString("password_hash", hashPassword(password))
            .apply()
    }

    private fun verifyPassword(password: String): Boolean {

        val savedHash = securePrefs.getString("password_hash", null)
            ?: return false

        return hashPassword(password) == savedHash
    }

    private fun showPasswordSetup() {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 80, 32, 32)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "🔐 Create Private Vault"
            textSize = 28f
            setTextColor(Color.BLACK)
        }

        val instructions = TextView(this).apply {
            text = """
                This is the first time you are opening Private Vault.

                Create a password that will be used to unlock your private vault.
            """.trimIndent()

            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, 25, 0, 25)
        }

        val passwordBox = EditText(this).apply {
            hint = "Create password"
            minLines = 1
        }

        val confirmBox = EditText(this).apply {
            hint = "Confirm password"
            minLines = 1
        }

        val createButton = Button(this).apply {
            text = "Create Vault"

            setOnClickListener {

                val password = passwordBox.text.toString()
                val confirmation = confirmBox.text.toString()

                when {
                    password.isEmpty() -> {
                        passwordBox.error = "Enter a password"
                    }

                    password.length < 6 -> {
                        passwordBox.error = "Password must be at least 6 characters"
                    }

                    password != confirmation -> {
                        confirmBox.error = "Passwords do not match"
                    }

                    else -> {
                        savePassword(password)
                        Toast.makeText(
                            this@MainActivity,
                            "Vault password created",
                            Toast.LENGTH_SHORT
                        ).show()

                        showMailbox()
                    }
                }
            }
        }

        layout.addView(title)
        layout.addView(instructions)
        layout.addView(passwordBox)
        layout.addView(confirmBox)

        layout.addView(
            createButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 30, 0, 0)
            }
        )

        setContentView(layout)
    }

    private fun showMailbox() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(248, 249, 250))
        }

        val header = TextView(this).apply {
            text = "✉  Mailbox"
            textSize = 28f
            setTextColor(Color.BLACK)
            setPadding(32, 45, 32, 30)
        }

        root.addView(header)

        val inbox = TextView(this).apply {
            text = "Inbox    •    100 messages"
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(32, 10, 32, 20)
        }

        root.addView(inbox)

        val scrollView = ScrollView(this)

        val messageList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        for (i in 1..100) {

            val message = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 20, 32, 20)
                setBackgroundColor(Color.WHITE)
            }

            val sender = TextView(this).apply {
                text = "Security Service"
                textSize = 16f
                setTextColor(Color.BLACK)
            }

            val subject = TextView(this).apply {
                text = "Authentication failed — security verification required"
                textSize = 15f
                setTextColor(Color.DKGRAY)
                setPadding(0, 6, 0, 4)
            }

            val time = TextView(this).apply {
                text = if (i == 1) {
                    "Just now"
                } else {
                    "$i minutes ago"
                }

                textSize = 12f
                setTextColor(Color.GRAY)
            }

            message.addView(sender)
            message.addView(subject)
            message.addView(time)

            message.setOnClickListener {
                showMessage(i)
            }

            messageList.addView(message)

            val divider = View(this).apply {
                setBackgroundColor(Color.LTGRAY)
            }

            messageList.addView(
                divider,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
                )
            )
        }

        scrollView.addView(messageList)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val composeButton = Button(this).apply {
            text = "✎  Compose"
            textSize = 16f

            setOnClickListener {
                showCompose()
            }
        }

        root.addView(
            composeButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(24, 12, 24, 24)
                gravity = Gravity.CENTER
            }
        )

        setContentView(root)
    }

    private fun showMessage(number: Int) {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 50, 32, 32)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "Security Service"
            textSize = 25f
            setTextColor(Color.BLACK)
        }

        val subject = TextView(this).apply {
            text = "Authentication failed"
            textSize = 20f
            setPadding(0, 25, 0, 20)
            setTextColor(Color.DKGRAY)
        }

        val body = TextView(this).apply {
            text = """
                We detected an unsuccessful authentication attempt.

                Message reference: SEC-$number

                If this activity was not expected, review your security settings.
            """.trimIndent()

            textSize = 16f
            setTextColor(Color.DKGRAY)
        }

        val back = Button(this).apply {
            text = "← Back to Inbox"

            setOnClickListener {
                showMailbox()
            }
        }

        layout.addView(title)
        layout.addView(subject)
        layout.addView(body)
        layout.addView(back)

        setContentView(layout)
    }

    private fun showCompose() {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 50, 32, 32)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "New Message"
            textSize = 28f
            setTextColor(Color.BLACK)
        }

        val instructions = TextView(this).apply {
            text = "Write a message"
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, 20, 0, 20)
        }

        val messageBox = EditText(this).apply {
            hint = "Type your message..."
            minLines = 6
            gravity = Gravity.TOP
        }

        val send = Button(this).apply {
            text = "Send"

            setOnClickListener {

                val enteredText = messageBox.text.toString()

                if (verifyPassword(enteredText)) {
                    showVault()
                } else {
                    messageBox.error = "Message could not be sent"
                }
            }
        }

        val back = Button(this).apply {
            text = "← Back to Inbox"

            setOnClickListener {
                showMailbox()
            }
        }

        layout.addView(title)
        layout.addView(instructions)
        layout.addView(messageBox)
        layout.addView(send)
        layout.addView(back)

        setContentView(layout)
    }

    private fun showVault() {

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 50, 32, 32)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "🔐 Private Vault"
            textSize = 28f
            setTextColor(Color.BLACK)
        }

        val message = TextView(this).apply {
            text = """
                Vault unlocked.

                Your protected applications will appear here.
            """.trimIndent()

            textSize = 17f
            setTextColor(Color.DKGRAY)
            setPadding(0, 25, 0, 30)
        }

        val placeholder = TextView(this).apply {
            text = "No protected apps added yet."
            textSize = 16f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(20, 50, 20, 50)
        }

        val lockButton = Button(this).apply {
            text = "🔒 Lock Vault"

            setOnClickListener {
                showMailbox()
            }
        }

        layout.addView(title)
        layout.addView(message)
        layout.addView(placeholder)
        layout.addView(lockButton)

        setContentView(layout)
    }
}