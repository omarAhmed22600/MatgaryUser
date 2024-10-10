package com.brandsin.user.ui.menu.payment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentPaymentBinding
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment

class PaymentFragment : BaseHomeFragment() {

    private var _binding: HomeFragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentViewModel by viewModels()

    private lateinit var walletAdapter: WalletAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.hajaty_wallet))

        viewModel.getWalletTransactions()

        initRecycler()
        setBtnListener()

        subscribeData()
    }

    private fun setBtnListener() {
        binding.root.setOnRefreshListener {
            viewModel.getWalletTransactions()
            binding.root.isRefreshing = false
        }

        binding.btnTransferPoints.setOnClickListener {
            findNavController().navigate(R.id.transferPointsFragment)
        }

        binding.btnRechargeBalance.setOnClickListener {
            // Create an instance of the bottom sheet dialog fragment with the data
            val bottomSheetFragment = RechargeBalanceFragment()
            // Show the bottom sheet dialog fragment
            bottomSheetFragment.show(childFragmentManager, "RechargeBalanceFragment") // tag
        }
    }

    private fun initRecycler() {
        binding.rvWallet.apply {
            walletAdapter = WalletAdapter()
            adapter = walletAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeData() {
        viewModel.getWalletTransactionsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    // binding.shimmer.visibility = View.GONE
                    // binding.rvRefundableProducts.visibility = View.VISIBLE
                    binding.walletBalance.text =
                        it.data?.currentCredit.toString() + " " + getString(R.string.currency)

                    walletAdapter.submitData(it.data?.transactions)
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // stop a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}