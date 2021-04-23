package com.github.calories.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView


class ScannerActivity : Activity(), ZXingScannerView.ResultHandler {
    private var mScannerView: ZXingScannerView? = null
    public override fun onCreate(state: Bundle?) {
        super.onCreate(state)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE
            );

        }
        else
        {
            setupScanner()
        }
    }

    private fun setupScanner() {
        mScannerView = ZXingScannerView(this) // Programmatically initialize the scanner view
        mScannerView!!.setAutoFocus(true)
        setContentView(mScannerView) // Set the scanner view as the content view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show()
                setupScanner()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    public override fun onResume() {
        super.onResume()

        if(mScannerView != null) {
            mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
            mScannerView!!.startCamera() // Start camera on resume
        }
    }

    public override fun onPause() {
        super.onPause()
        if(mScannerView != null)
            mScannerView!!.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        // Do something with the result here
        Log.v(TAG, rawResult.text) // Prints scan results
        Log.v(
            TAG,
            rawResult.barcodeFormat.toString()
        ) // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);

        val returnIntent = Intent()
        returnIntent.putExtra("value", rawResult.text)
        setResult(RESULT_OK, returnIntent)

        finish()
    }

    companion object {
        private const val TAG = "SimpleScannerActivity"
        private const val CAMERA_REQUEST_CODE = 0x1
    }
}