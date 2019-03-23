package dev.christianbroomfield.d100web.util

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus

fun Response.answerShouldBe(expected: String) {
    assertThat(this, hasStatus(Status.OK).and(hasBody(expected)))
}
