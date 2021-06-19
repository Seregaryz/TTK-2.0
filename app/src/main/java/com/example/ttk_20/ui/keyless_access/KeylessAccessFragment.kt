package com.example.ttk_20.ui.keyless_access

import android.animation.Animator
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.example.ttk_20.databinding.FrKeylessAccessBinding
import com.example.ttk_20.helper.BluetoothHelper
import com.example.ttk_20.helper.BluetoothListener
import com.example.ttk_20.ui.qr.QrCodeBottomSheetFragment
import com.example.ttk_20.ui.settings.SettingsBottomSheetFragment
import com.example.ttk_20.utils.addSystemTopPadding
import com.example.ttk_20.utils.visible
import com.example.ttk_20.R
import dagger.hilt.android.AndroidEntryPoint
import org.altbeacon.beacon.*
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class KeylessAccessFragment : Fragment(), BeaconConsumer,
    BluetoothListener.OnBluetoothSupportedCheckListener,
    BluetoothListener.OnBluetoothEnabledCheckListener,
    BluetoothListener.BluetoothTrigger {

    private lateinit var _binding: FrKeylessAccessBinding
    private val binding get() = _binding

    private val viewModel: KeylessAccessViewModel by viewModels()

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                isBluetoothEnabled = true
                startTransmit()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Bluetooth permission denied",
                    Toast.LENGTH_LONG
                ).show()
                isBluetoothEnabled = false
            }
        }

    private var isBluetoothEnabled = false

    @Inject
    lateinit var beaconManager: BeaconManager

    @Inject
    lateinit var bluetoothHelper: BluetoothHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FrKeylessAccessBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.addSystemTopPadding()
        binding.btnSettings.setOnClickListener {
            SettingsBottomSheetFragment().show(childFragmentManager, null)
        }
        binding.btnGetQr.setOnClickListener {
            QrCodeBottomSheetFragment().show(childFragmentManager, null)
        }
        bluetoothHelper.initializeBluetooth(this)
        binding.btnOpenDoor.setOnClickListener {
            binding.apply {
                progressBar.visible(true)
                doorIcon.visible(false)
                progressBar.apply {
                    playAnimation()
                    addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            tvOpen.apply {
                                text = getString(R.string.tv_opened)
                                setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                            }
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                    })
//                    addAnimatorPauseListener(object : Animator.AnimatorPauseListener {
//                        override fun onAnimationPause(animation: Animator?) {
//                            tvOpen.apply {
//                                text = getString(R.string.tv_opened)
//                                setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
//                            }
//                        }
//
//                        override fun onAnimationResume(animation: Animator?) {
//                            TODO("Not yet implemented")
//                        }
//
//                    })
                    addValueCallback(
                        KeyPath("**"),
                        LottieProperty.COLOR_FILTER,
                        {
                             if (it.overallProgress < 0.6)
                            PorterDuffColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    com.example.ttk_20.R.color.colorSenary
                                ),
                                PorterDuff.Mode.SRC_ATOP
                            ) else  PorterDuffColorFilter(
                                 ContextCompat.getColor(
                                     requireContext(),
                                     com.example.ttk_20.R.color.green
                                 ),
                                 PorterDuff.Mode.SRC_ATOP
                             )
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableBluetooth()
    }

    override fun onBeaconServiceConnect() {

    }

    override fun getApplicationContext(): Context = requireActivity().applicationContext

    override fun unbindService(p0: ServiceConnection?) {
    }

    override fun bindService(p0: Intent?, p1: ServiceConnection?, p2: Int): Boolean {
        return false
    }

    private fun startTransmit() {
        beaconManager.bind(this)
        val beacon = Beacon.Builder()
            .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x004c)
            .setTxPower(-59)
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        val beaconTransmitter = BeaconTransmitter(requireContext(), beaconParser)
        beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                Timber.e("Advertisement start failed with code: $errorCode")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                Timber.i("Advertisement start succeeded.")
            }
        })
    }

    override fun onBLENotSupported() {
        Toast.makeText(requireContext(), "BLE not supported", Toast.LENGTH_LONG).show();
    }

    override fun onBluetoothNotSupported() {
        Toast.makeText(requireContext(), "Blutooth not supported", Toast.LENGTH_LONG).show();
    }

    override fun onBluetoothEnabled(enable: Boolean) {
        if (enable) {
            isBluetoothEnabled = true
            startTransmit()
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableBtIntent)
        }
    }

    override fun initBluetooth() {
        bluetoothHelper.initializeBluetooth(this);
    }

    override fun enableBluetooth() {
        bluetoothHelper.enableBluetooth(this)
    }

}