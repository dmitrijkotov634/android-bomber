package com.dm.bomber.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.dm.bomber.R
import com.dm.bomber.databinding.DialogProxiesBinding
import com.dm.bomber.ui.MainRepository
import com.dm.bomber.ui.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProxiesDialog : DialogFragment() {

    private var _binding: DialogProxiesBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val repository: MainRepository by lazy { MainRepository(requireContext()) }

    private val model: MainViewModel by activityViewModels<MainViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogProxiesBinding.inflate(getLayoutInflater())
        _binding!!.proxies.setText(repository.rawProxy)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.proxy)
            .setView(binding.getRoot())
            .setPositiveButton(android.R.string.ok, null)
            .create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.getRoot()
    }

    override fun onStart() {
        super.onStart()

        (dialog as? AlertDialog)?.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            try {
                repository.parseProxy(
                    if (binding.proxies.getText() == null)
                        "" else binding.proxies.getText().toString()
                )

                repository.rawProxy = binding.proxies.getText().toString()
                model.setProxyEnabled(repository.proxy.isNotEmpty())

                dismiss()
            } catch (e: ArrayIndexOutOfBoundsException) {
                Toast.makeText(requireContext(), R.string.proxy_format_error, Toast.LENGTH_SHORT).show()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(requireContext(), R.string.proxy_format_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
