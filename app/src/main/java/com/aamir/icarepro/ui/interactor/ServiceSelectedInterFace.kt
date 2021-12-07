package com.aamir.icarepro.ui.interactor

import com.aamir.icarepro.data.models.service.ServiceData


interface ServiceSelectedInterFace {
    fun onServiceSelect(service: ServiceData)
}