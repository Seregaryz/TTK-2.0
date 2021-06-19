package com.example.ttk_20.ui.qr

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.ttk_20.BuildConfig
import com.example.ttk_20.R
import com.example.ttk_20.databinding.FrQrCodeBottomSheetBinding
import com.example.ttk_20.utils.glideAvatarLarge
import com.example.ttk_20.utils.registerOnBackPressedCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.*


@AndroidEntryPoint
class QrCodeBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: FrQrCodeBottomSheetBinding
    private val binding get() = _binding

    private val viewModel: QrCodeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FrQrCodeBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerOnBackPressedCallback {
            dismiss()
        }
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        binding.btnShareQr.setOnClickListener {
//            Glide.with(this)
//                .asBitmap()
//                .load("${BuildConfig.BASE_URL}v1/code")
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .into(object : CustomTarget<Bitmap>() {
//                    override fun onResourceReady(
//                        resource: Bitmap,
//                        transition: Transition<in Bitmap>?
//                    ) {
//                            val share = Intent(Intent.ACTION_SEND)
//                            share.type = "image/png"
//                            val f = createImageFile()
//                            val bos = ByteArrayOutputStream()
//                            resource.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
//                            val bitmapdata = bos.toByteArray()
//
//                            val fos = FileOutputStream(f)
//                            fos.write(bitmapdata)
//                            fos.flush()
//                            fos.close()
//                            share.putExtra(Intent.EXTRA_STREAM, f.toUri())
//                            startActivity(Intent.createChooser(share, "Share Image"))
//
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) {
//                        // this is called when imageView is cleared on lifecycle call or for
//                        // some other reason.
//                        // if you are referencing the bitmap somewhere else too other than this imageView
//                        // clear it here as you can no longer have the bitmap
//                    }
//                })
            if (verifyPermissions()) downloadImage("BuildConfig.BASE_URL}v1/code")

//            val share = Intent(Intent.ACTION_SEND)
//            share.type = "image/png"
//            val f = createImageFile()
//            val bos = ByteArrayOutputStream()
//            binding.imageQr.drawable.toBitmap(300, 300).compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
//            val bitmapdata = bos.toByteArray()
//
////write the bytes in file
//
////write the bytes in file
//            val fos = FileOutputStream(f)
//            fos.write(bitmapdata)
//            fos.flush()
//            fos.close()
//            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f))
//            startActivity(Intent.createChooser(share, "Share Image"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight: Int = resources.displayMetrics.heightPixels + 128
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
    }

    override fun onResume() {
        super.onResume()
        binding.imageQr.glideAvatarLarge("code")
//        viewModel.apply {
//            qrCode.subscribe {
//
//            }
//        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        val storageDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "png_${System.currentTimeMillis()}",
            ".png",
            storageDir
        )
    }

    fun downloadImage(imageURL: String) {
        if (!verifyPermissions()) {
            return
        }
        val dirPath =
            Environment.getExternalStorageDirectory().absolutePath + "/" + getString(R.string.app_name) + "/"
        //val dir = File(dirPath)
        val dir = createImageFile()
        val fileName = dir.absolutePath.substring(imageURL.lastIndexOf('/') + 1)
        Glide.with(this)
            .asDrawable()
            .load(imageURL)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(object : CustomTarget<Drawable?>() {

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    Toast.makeText(
                        requireContext(),
                        "Failed to Download Image! Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    val bitmap = (resource as BitmapDrawable).bitmap
                    Toast.makeText(requireContext(), "Saving Image...", Toast.LENGTH_SHORT).show()
                    saveImage(bitmap, dir, fileName)
                }
            })
    }

    fun verifyPermissions(): Boolean {

        // This will return the current Status
        val permissionExternalMemory =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val STORAGE_PERMISSIONS = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(requireActivity(), STORAGE_PERMISSIONS, 1)
            return false
        }
        return true
    }

    private fun saveImage(image: Bitmap, storageDir: File, imageFileName: String) {
        var successDirCreated = false
        if (!storageDir.exists()) {
            successDirCreated = storageDir.mkdir()
        }
        if (successDirCreated) {
            val imageFile = File(storageDir, imageFileName)
            val savedImagePath = imageFile.absolutePath
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(CompressFormat.JPEG, 100, fOut)
                fOut.close()
                Toast.makeText(requireContext(), "Image Saved!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error while saving image!", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(requireContext(), "Failed to make folder!", Toast.LENGTH_SHORT).show()
        }
    }

}