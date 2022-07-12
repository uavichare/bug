package com.example.buglibrary.ui.notifications

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.buglibrary.databinding.NotificationFragmentBinding
import com.example.buglibrary.utils.ConversionUtils
import java.util.*

class NotificationFragmentBeacon : Fragment() {


    private lateinit var viewModel: NotificationViewModel
    private var _binding: NotificationFragmentBinding? = null
    private val binding get() = _binding!!
    private var bleScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    private lateinit var scanCallback: ScanCallback
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotificationFragmentBinding.inflate(inflater, container, false)

//        binding.textBeaconData.text = "Detecting beacon"
        scanCallBack()
        bleScanner.startScan(scanCallback)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)

    }

    override fun onPause() {
        super.onPause()
        bleScanner.stopScan(scanCallback)
    }

    private fun scanCallBack() {
        //do heavy work on a background thread
        //stopSelf();
        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                Toast.makeText(context, "scan single", Toast.LENGTH_SHORT).show()
                when (callbackType) {
                    ScanSettings.CALLBACK_TYPE_MATCH_LOST -> {

                    }
                    else -> {
                        readBeaconData(result)
                    }
                }

            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                super.onBatchScanResults(results)
                //Toast.makeText(getApplicationContext(), "scan multiple", Toast.LENGTH_SHORT).show();
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Toast.makeText(context, "scan failed$errorCode", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun readBeaconData(result: ScanResult) {

        if (result.scanRecord != null) {
            val txtPower = "Tx power -> ${result.scanRecord?.txPowerLevel}"
            val deviceName = "Device name-> ${result.scanRecord?.deviceName}"

            val scanRecord = result.scanRecord?.bytes
            var startByte = 2
            var patternFound = false
            while (startByte <= 5) {
                if (scanRecord!![startByte + 2]
                        .toInt() and 0xff == 0x02 &&  // identifies an iBeacon
                    scanRecord[startByte + 3].toInt() and 0xff == 0x15
                ) {
                    // identifies correct data length
                    patternFound = true
                    break
                }
                startByte++
            }

            if (patternFound) {
                // get the UUID from the hex result
                val uuidBytes = ByteArray(16)
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16)
                val uuid = ConversionUtils.bytesToUuid(uuidBytes)

                // get the major from hex result
                val major = ConversionUtils.byteArrayToInteger(
                    Arrays.copyOfRange(
                        scanRecord,
                        startByte + 20,
                        startByte + 22
                    )
                )

                // get the minor from hex result
                val minor = ConversionUtils.byteArrayToInteger(
                    Arrays.copyOfRange(
                        scanRecord,
                        startByte + 22,
                        startByte + 24
                    )
                )
                val str = StringBuilder()
                str.appendLine(txtPower)
                str.appendLine(deviceName)
                str.appendLine("uuid = $uuid")
                str.appendLine("Minor = $minor")
                str.appendLine("Major = $major")
                str.append("Battery ${scanRecord?.get(0)}")
//                binding.textBeaconData.append(str)

            }


        }
    }


}