package com.example.myfirstapp.utils

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class PermissionHandler (
    private var onPermissionGranted: ((String) -> Unit)? = null,
    private var onPermissionDenied: (() -> Unit)? = null,
    activity: ComponentActivity
){

    private var singlePermission : ActivityResultLauncher<String>? =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if(isGranted){

        }else{

        }

    }

    private var multiplePermissions : ActivityResultLauncher<Array<String>>? =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ resultMap ->
            resultMap.entries.forEach { entry ->
                if(entry.value){
                    onPermissionGranted?.invoke(entry.key)
                }else{
                    onPermissionDenied?.invoke()
                }

            }

    }

    fun requestMultiplePermissions(permission: List<String>){
        multiplePermissions?.launch(permission.toTypedArray())
    }

    fun cleanupResources(){
        singlePermission = null
    }

    private fun Map.Entry<String, Boolean>.checkIsGranted(): Boolean{
        return this.value
    }

}