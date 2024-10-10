package com.brandsin.user.ui.menu.refundableProducts

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.brandsin.user.databinding.ReasonReturnDailogBinding
import com.brandsin.user.model.refundableProduct.ReasonReturnItem
import com.brandsin.user.ui.menu.refundableProducts.adapter.ReasonReturnAdapter

class ReasonReturnDialog(
    private val context: Context,
    private val itemList: List<ReasonReturnItem?>?,
    private val onItemClick: (ReasonReturnItem) -> Unit
) : Dialog(context) {

    private lateinit var binding: ReasonReturnDailogBinding

    private lateinit var reasonReturnAdapter: ReasonReturnAdapter

    private val onItemClickCallBack: (reasonReturnItem: ReasonReturnItem) -> Unit =
        { reasonReturnItem ->
            onItemClick(reasonReturnItem)
            dismiss() // Dismiss the dialog after selecting an item
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReasonReturnDailogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.icCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        binding.rvReasonReturn.apply {
            reasonReturnAdapter = ReasonReturnAdapter(onItemClickCallBack)
            reasonReturnAdapter.submitData(itemList)
            adapter = reasonReturnAdapter
        }
    }
}