package com.trx.consumer.models.common

class PermissionModel {

    // Dummy property for permissions
    var id: String = ""
    var name: String = ""

    companion object {
        fun testList(): List<PermissionModel> {
            return listOf(
                PermissionModel().apply {
                    id = "cameraId"
                    name = "Camera Permissions"
                },
                PermissionModel().apply {
                    id = "microphoneId"
                    name = "Microphone Permissions"
                }
            )
        }
    }
}
