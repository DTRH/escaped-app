package com.pedersen.escaped.master.controls.webcams

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import com.pedersen.escaped.R
import com.potterhsu.rtsplibrary.NativeCallback
import com.potterhsu.rtsplibrary.RtspClient



class WebcamActivity : AppCompatActivity() {

    private lateinit var ip: String
    private var rtspClient: RtspClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webcam)

        val ivPreview: ImageView = findViewById(R.id.camera_feed)

        val rtspClient = RtspClient(NativeCallback { frame, nChannel, width, height ->
            ivPreview.post {
                val area = width * height
                val pixels = IntArray(area)
                for (i in 0 until area) {
                    var r = frame[3 * i].toInt()
                    var g = frame[3 * i + 1].toInt()
                    var b = frame[3 * i + 2].toInt()
                    if (r < 0) r += 255
                    if (g < 0) g += 255
                    if (b < 0) b += 255
                    pixels[i] = Color.rgb(r, g, b)
                }
                val bmp = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.RGB_565)
                ivPreview.setImageBitmap(bmp)
            }
        })

        ip = intent.extras.get(CAMERA_IP) as String

        val endpoint = ip
        if (endpoint.isEmpty()) {
            Toast.makeText(this, "Endpoint is empty", Toast.LENGTH_SHORT).show()
            return
        }

        Thread(Runnable {
            while (true) {
                if (rtspClient.play("rtsp://anonymity:anonymity@$endpoint") == 0)
                    break

                runOnUiThread {
                    Toast.makeText(this@WebcamActivity,
                                   "Connection error, retry after 3 seconds",
                                   Toast.LENGTH_SHORT).show()
                }

                try {
                    Thread.sleep(3000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }).start()
    }

    override fun onDestroy() {
        rtspClient?.stop()
        rtspClient?.dispose()
        super.onDestroy()
    }

    companion object {

        const val CAMERA_IP = "webcam_ip_address"

        fun newIntent(context: Context, ip: String): Intent {
            val args = Bundle(1)
            args.putString(CAMERA_IP, ip)
            val intent = Intent(context, WebcamActivity::class.java)
            intent.putExtras(args)
            return intent
        }
    }
}
