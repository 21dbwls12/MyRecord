package com.example.editmyrecord

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationProvider
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import com.example.editmyrecord.ui.theme.BlueGreen
import com.example.editmyrecord.ui.theme.EditMyRecordTheme
import com.example.editmyrecord.ui.theme.Grey900
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState


class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditMyRecordTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MapScreen()
                }
            }
        }
    }
}

@Composable
fun MapScreen() {
    val context = LocalContext.current

    Column {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            MapViewScreen(context = context)
        }
        Column {
            NavigateBar(
                homeColor = Grey900,
                searchColor = Grey900,
                mapColor = BlueGreen,
                myRecordColor = Grey900,
                context = context
            )
        }
    }
}

@Composable
fun MapViewScreen(context: Context) {
    val center = LatLng(35.95, 127.95)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(center, 7.15f)
    }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
//    val fusedLocationClient = remember(context) {
//        LocationServices.getFusedLocationProviderClient(context)
//    }
//    val locationPermissionRequest = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { permissions ->
//        if (permissions) {
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                if (location != null) {
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                }
//            }
//        }
//    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker()

        }
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
//                        val fusedLocationClient =
//                            LocationServices.getFusedLocationProviderClient(context)
//                        if (ActivityCompat.checkSelfPermission(
//                                context,
//                                Manifest.permission.ACCESS_FINE_LOCATION
//                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                                context,
//                                Manifest.permission.ACCESS_COARSE_LOCATION
//                            ) != PackageManager.PERMISSION_GRANTED
//                        ) {
//                            val locationPermissionRequest = rememberLauncherForActivityResult(
//                                contract = ActivityResultContracts.RequestMultiplePermissions()
//                            ) { permissions ->
//                                when {
//                                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//
//                                    }
//                                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//
//                                    }
//                                    else -> {  }
//                                }
//                            }
//                            ActivityResultContracts.RequestMultiplePermissions() { permissions ->
//
//                            }
//                            ActivityCompat.requestPermissions(
//                                context as Activity,
//                                arrayOf(
//                                    Manifest.permission.ACCESS_FINE_LOCATION,
//                                    Manifest.permission.ACCESS_COARSE_LOCATION
//                                ),
//                                requestCode
//                            )
//                            return@IconButton
//                        }
//                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                            if (location != null) {
//                                currentLocation = LatLng(location.latitude, location.longitude)
//                                cameraPositionState.position = CameraPosition.fromLatLngZoom(
//                                    LatLng(location.latitude, location.longitude), 12f
//                                )
//                                    LatLng(location.latitude, location.longitude)
//                                )
//                            }
//                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.mylocation),
                        contentDescription = "현재 위치",
                        tint = Grey900
                    )
                    currentLocation?.let { location ->
                        Text(text = "${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }
}
