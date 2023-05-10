package ru.lyubeznyh.filemanager.presentation.filespage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.lyubeznyh.filemanager.BuildConfig
import ru.lyubeznyh.filemanager.R
import ru.lyubeznyh.filemanager.databinding.FragmentFilesBinding
import ru.lyubeznyh.filemanager.domain.model.FileModel
import ru.lyubeznyh.filemanager.utilities.simpleRenderResult
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.R)
class FilesFragment : Fragment(), HasAndroidInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var viewModelsFactory: FileViewModelsFactory
    private val viewModel: FilesViewModel by viewModels { viewModelsFactory }

    private var _binding: FragmentFilesBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { FilesAdapter(::onClickFile) }

    private val rootPath: String? by lazy { arguments?.getString(PATH_ARGUMENT) }

    private val launcherPermission by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) viewModel.setFiles(rootPath)
            else viewModel.setError(IllegalAccessException("Not permission"))
        }
    }

    private val launcherResultActivity by lazy {
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (Environment.isExternalStorageManager()) {
                viewModel.setFiles(rootPath)
            } else {
                viewModel.setError(IllegalAccessException("Not permission"))
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilesBinding.inflate(inflater)
        checkPermissionAndSetFiles(rootPath)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFiles.adapter = adapter
        subscribeToResult()
    }

    //Rendering UI depending on the result
    private fun subscribeToResult() {
        viewModel.result.onEach { result ->
            simpleRenderResult(
                binding.root,
                result,
                { viewModel.setFiles(rootPath) }) { filesList ->
                addListenerOnClickShowMenu()
                renderListOrEmpty(filesList)
                subscribeToSortingSettings()
                subscribeToReverseSettings()
                onClickModified()
                addListenerOnReversed()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    //Subscribes to the sorting setting and sets the correct text
    private fun subscribeToSortingSettings() {
        viewModel.sorted.onEach {
            when (it) {
                FilesViewModel.SORT_CREATION_TIME -> binding.toolBar.tvSortMenu.text =
                    getString(R.string.creation_time)
                FilesViewModel.SORT_EXTENSION -> binding.toolBar.tvSortMenu.text =
                    getString(R.string.extension)
                FilesViewModel.SORT_SIZE -> binding.toolBar.tvSortMenu.text =
                    getString(R.string.size)
                FilesViewModel.SORT_ALPHABETICALLY -> binding.toolBar.tvSortMenu.text =
                    getString(R.string.alphabetically)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    //Subscribes to the U-turn setting and sets the correct image
    private fun subscribeToReverseSettings() {
        viewModel.isReversed.onEach {
            val iconArrow = if (it) R.drawable.ic_arrow_downward else R.drawable.ic_arrow_upward
            Glide.with(requireContext())
                .load(iconArrow)
                .into(binding.toolBar.ivReversSorted)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    //Filters the list in if there are changes
    private fun onClickModified() {
        binding.toolBar.cbShowModified.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) adapter.submitList(viewModel.getFilterModified())
            else adapter.submitList(viewModel.getNotFilterModified())
        }
    }

    private fun addListenerOnReversed() {
        binding.toolBar.ivReversSorted.setOnClickListener {
            viewModel.setReverse(!viewModel.isReversed.value)
        }
    }

    //Sets the correct UI if the list is empty or full
    private fun renderListOrEmpty(fileList: List<FileModel>) {
        with(binding) {
            if (fileList.isEmpty()) {
                tvEmptyString.visibility = View.VISIBLE
                rvFiles.visibility = View.GONE
                tvEmptyString.text = getString(R.string.empty)
            } else {
                tvEmptyString.visibility = View.GONE
                rvFiles.visibility = View.VISIBLE
                adapter.submitList(fileList) { rvFiles.scrollToPosition(FIRS_ITEM) }
            }
        }
    }

    //A callback function passed to the adapter that handles clicking on an item
    private fun onClickFile(file: FileModel) {
        val arguments = Bundle().apply {
            putString(PATH_ARGUMENT, file.path)
        }
        parentFragmentManager.commit {
            replace(R.id.mainFragmentContainer, FilesFragment::class.java, arguments)
            addToBackStack(this::class.java.name)
        }
    }

    private fun addListenerOnClickShowMenu() {
        binding.toolBar.tvSortMenu.setOnClickListener { showMenu(it, R.menu.sort_menu) }
    }

    //Shows the sorting menu and sets the setting in the viewModel
    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.sizeMenu -> viewModel.setSorted(FilesViewModel.SORT_SIZE)
                R.id.alphabeticallyMenu -> viewModel.setSorted(FilesViewModel.SORT_ALPHABETICALLY)
                R.id.extensionMenu -> viewModel.setSorted(FilesViewModel.SORT_EXTENSION)
                R.id.creationTimeMenu -> viewModel.setSorted(FilesViewModel.SORT_CREATION_TIME)
            }
            true
        }
        popup.show()
    }

    /*Checks permissions and, if there are any,
    starts downloading files, otherwise launches the launcher to get permissions.*/
    private fun checkPermissionAndSetFiles(path: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                launcherResultActivity
                    .launch(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri))
            } else {
                viewModel.setFiles(path)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.setFiles(path)
            } else {
                launcherPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun androidInjector(): AndroidInjector<Any> = fragmentInjector

    companion object {
        const val PATH_ARGUMENT = "PATH_ARGUMENT"
        const val FIRS_ITEM = 0
    }
}


