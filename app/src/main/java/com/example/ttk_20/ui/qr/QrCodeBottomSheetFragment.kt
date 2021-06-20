package com.example.ttk_20.ui.qr

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
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
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
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
import java.util.*


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
        val f = createImageFile()
        binding.btnShareQr.setOnClickListener {
            Glide.with(this)
                .asBitmap()
                .load("${BuildConfig.BASE_URL}v1/code")
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "image/png"
                        //val f = createImageFile()
                        val bos = ByteArrayOutputStream()
                        //binding.imageQr.invalidate()
//                        val bitmap = (binding.imageQr.drawable as VectorDrawable).toBitmap(300, 300)
                        resource.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
                        val bitmapdata = bos.toByteArray()
                        val fos = FileOutputStream(f)
                        try {
                            fos.write(bitmapdata)
                            fos.flush()
                            fos.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.fileprovider",
                            f
                        )
                        share.putExtra(Intent.EXTRA_STREAM, photoURI)
                        startActivity(Intent.createChooser(share, "Share Image"))

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
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

}