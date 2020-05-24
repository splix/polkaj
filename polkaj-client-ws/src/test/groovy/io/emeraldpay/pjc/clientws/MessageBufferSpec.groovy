package io.emeraldpay.pjc.clientws

import spock.lang.Specification

import java.net.http.WebSocket

class MessageBufferSpec extends Specification {

    MessageBuffer buffer = new MessageBuffer()

    def "For empty return itself"() {
        setup:
        def ws = Stub(WebSocket)
        when:
        def act = buffer.last(ws, "Hello World!")
        then:
        act == "Hello World!"
    }

    def "Join multiple"() {
        setup:
        def ws = Stub(WebSocket)
        when:
        buffer.add(ws, "Hello ")
        buffer.add(ws, "World")
        def act = buffer.last(ws, "!")
        then:
        act == "Hello World!"
    }

    def "Removes buffer after last"() {
        setup:
        def ws = Stub(WebSocket)
        when:
        def act = buffer.last(ws, "Old")
        then:
        act == "Old"

        when:
        act = buffer.last(ws, "New")
        then:
        act == "New"
    }
}
