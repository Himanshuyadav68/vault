package com.privatevault

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Temporary secret for this prototype.
    // We will replace this with secure storage later.
    private val secret = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showMailbox()
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

                if (enteredText == secret) {
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

        layout.addView(
            lockButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        setContentView(layout)
    }
}