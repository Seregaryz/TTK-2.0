package com.example.ttk_20.ui.keyless_access

import android.animation.Animator
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.example.ttk_20.databinding.FrKeylessAccessBinding
import com.example.ttk_20.helper.BluetoothHelper
import com.example.ttk_20.helper.BluetoothListener
import com.example.ttk_20.ui._base.BaseFragment
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
class KeylessAccessFragment : BaseFragment(), BeaconConsumer,
    BluetoothListener.OnBluetoothSupportedCheckListener,
    BluetoothListener.OnBluetoothEnabledCheckListener,
    BluetoothListener.BluetoothTrigger {

    private lateinit var _binding: FrKeylessAccessBinding
    private val binding get() = _binding

    private var isRepeat = true

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
            viewModel.openDoor()
        }
        binding.apply {
            progressBar.addAnimatorPauseListener(object : Animator.AnimatorPauseListener {
                override fun onAnimationPause(animation: Animator?) {
                    tvOpen.apply {
                        text = getString(R.string.tv_opened)
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    }
                    progressBar.addValueCallback(
                        KeyPath("**"),
                        LottieProperty.COLOR_FILTER,
                        {
                            PorterDuffColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                ),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        })
                }

                override fun onAnimationResume(animation: Animator?) {
                    tvOpen.apply {
                        text = getString(R.string.tv_open)
                        setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorSenary
                            )
                        )
                    }
                    progressBar.addValueCallback(
                        KeyPath("**"),
                        LottieProperty.COLOR_FILTER,
                        {
                            PorterDuffColorFilter(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorSenary
                                ),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        })
                }

            })

            progressBar.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    progressBar.visible(false)
                    doorIcon.visible(true)
//                    tvOpen.apply {
//                        text = getString(R.string.tv_open)
//                        setTextColor(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                R.color.colorSenary
//                            )
//                        )
//                    }
//                    progressBar.addValueCallback(
//                        KeyPath("**"),
//                        LottieProperty.COLOR_FILTER,
//                        {
//                            PorterDuffColorFilter(
//                                ContextCompat.getColor(
//                                    requireContext(),
//                                    R.color.colorSenary
//                                ),
//                                PorterDuff.Mode.SRC_ATOP
//                            )
//                        })
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                    progressBar.pauseAnimation()
                    viewModel.makeAnimationOffset()
                    if (isRepeat) {
                        tvOpen.apply {
                            text = getString(R.string.tv_opened)
                            setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                        }
                        progressBar.addValueCallback(
                            KeyPath("**"),
                            LottieProperty.COLOR_FILTER,
                            {
                                PorterDuffColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.green
                                    ),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                            })
                    }
                    isRepeat = false
                }

            })

        }
    }

    override fun onResume() {
        super.onResume()
        enableBluetooth()
        viewModel.apply {
            animationOffset.subscribe {
                if (it) {
                    binding.progressBar.apply {
                        resumeAnimation()
                        binding.tvOpen.apply {
                            text = getString(R.string.tv_open)
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.colorSenary
                                )
                            )
                        }
                        addValueCallback(
                            KeyPath("**"),
                            LottieProperty.COLOR_FILTER,
                            {
                                PorterDuffColorFilter(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.colorSenary
                                    ),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                            })
                    }
                    dispose()
                } else {
                    viewModel.makeAnimationOffset()
                }
            }
            successOpen.subscribe {
                binding.apply {
                    progressBar.visible(true)
                    doorIcon.visible(false)
                    progressBar.apply {
                        playAnimation()
                    }
                }
            }.disposeOnPause()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.progressBar.apply {
            cancelAnimation()
            visible(false)
        }
        binding.doorIcon.visible(true)
        binding.tvOpen.apply {
            text = getString(R.string.tv_open)
            setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorSenary
                )
            )
        }
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
//        beaconManager.bind(this)
//        val builder: Notification.Builder = Notification.Builder(requireContext())
//        builder.setSmallIcon(R.drawable.ic_app_icon)
//        builder.setContentTitle("Scanning for Beacons")
//        val intent = Intent(requireContext(), KeylessAccessViewModel::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        builder.setContentIntent(pendingIntent)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "My Notification Channel ID",
//                "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT
//            )
//            channel.description = "My Notification Channel Description"
//            val notificationManager: NotificationManager =
//                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//            builder.setChannelId(channel.id)
//        }
//        beaconManager.enableForegroundServiceScanning(builder.build(), 456)
//        beaconManager.setEnableScheduledScanJobs(false)
//        val beacon = Beacon.Builder()
//            .setId1("D35B76E2-E01C-9FAC-BA8D-7CE20BDBA0C6")
//            .setId2("2021")
//            .setId3("100")
//            .setManufacturer(0x004c)
//            .setTxPower(-59)
//            .build()
//        val beaconParser = BeaconParser()
//            .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
//        val beaconTransmitter = BeaconTransmitter(requireContext(), beaconParser)
//        beaconTransmitter.advertiseMode = AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
//        beaconTransmitter.startAdvertising(beacon, object : AdvertiseCallback() {
//            override fun onStartFailure(errorCode: Int) {
//                Timber.e("Advertisement start failed with code: $errorCode")
//            }
//
//            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
//                Timber.i("Advertisement start succeeded.")
//            }
//        })
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