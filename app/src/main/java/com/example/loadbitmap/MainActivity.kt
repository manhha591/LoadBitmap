package com.example.loadbitmap
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val viewModel: MyViewModel by viewModels()
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView: ImageView = findViewById(R.id.imageView)
        val imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSe8alpXd2lRM-ePBlcVWexXEZk_rFXy389H5C3rp0ypIkD4zO5cbgmDpPE0a94kV9vbDc&usqp=CAU"

        // Setup ViewModel observer
        viewModel.bitmapLiveData.observe(this) { bitmap ->
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
            } else {
                // Xử lý lỗi tải hình ảnh
                Log.e("MainActivity", "Failed to load bitmap")
            }
        }

        // Setup network callback
        setupNetworkCallback()

        // Register network callback
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_IMS).build(),
            networkCallback
        )
    }

    private fun setupNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                var url ="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSe8alpXd2lRM-ePBlcVWexXEZk_rFXy389H5C3rp0ypIkD4zO5cbgmDpPE0a94kV9vbDc&usqp=CAU"
                Log.d("NetworkCallback", "Network is available")
                // Khi mạng khả dụng, tải hình ảnh
                viewModel.loadBitmap(url)
                val connection= network.openConnection(URL(url)) as HttpURLConnection

            }

            override fun onLost(network: Network) {
                Log.d("NetworkCallback", "Network is lost")
                // Thực hiện hành động khi mất kết nối (ví dụ: hiển thị thông báo)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hủy đăng ký lắng nghe mạng khi Activity bị hủy
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
