package com.pritam.githubexplorer

import com.pritam.githubexplorer.retrofit.model.Error
import com.pritam.githubexplorer.retrofit.model.ErrorResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ModelTest {

    private var err1: Error = Error("https://github.com/pritamkhose", "200", "No Msg")
    private var err2: Error = Error("null", "500", "Error Msg")
    private var errList = mutableListOf<Error>()
    private var er: ErrorResponse = ErrorResponse("https://github.com/pritamkhose", errList, "No Msg")

    @Before
    fun setUp() {
        errList.add(err1)
        errList.add(err2)
    }

    @Test
    fun `Error model data class test`() {
        println(er.toString())
        Assert.assertEquals(er.message, "No Msg")
        Assert.assertEquals(er.errors.size, 2)
    }

}