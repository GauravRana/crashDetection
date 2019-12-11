package com.example.biker112.ui.apiManager


object SearchRepositoryProvider {



    fun provideMainRepository(url: String): SearchRepository {
        return SearchRepository(ApiService.Factory.createMain(url))
    }


    fun provideOtherRepository(url: String): SearchRepository {
        return SearchRepository(ApiService.Factory.createOther(url))
    }

}