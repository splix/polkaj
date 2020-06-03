package io.emeraldpay.polkaj.scaletypes

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class MetadataSpec extends Specification {

    def "Equals for Metadata"() {
        when:
        def v = EqualsVerifier.forClass(Metadata)
            .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Module"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Module)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Storage"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Storage)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Storage.Entry"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Storage.Entry)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Storage.Type"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Storage.Type)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Storage.MapDefinition"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Storage.MapDefinition)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Storage.DoubleMapDefinition"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Storage.DoubleMapDefinition)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Call"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Call)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Call.Arg"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Call.Arg)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Constant"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Constant)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Event"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Event)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals for Error"() {
        when:
        def v = EqualsVerifier.forClass(Metadata.Error)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Cast Plain type to String"() {
        setup:
        Metadata.Storage.Type<?> t = new Metadata.Storage.PlainType()
        when:
        Metadata.Storage.Type<String> act = t.cast(String)
        then:
        act == t
    }

    def "Cannot cast Plain type to Map"() {
        setup:
        Metadata.Storage.Type<?> t = new Metadata.Storage.PlainType()
        when:
        t.cast(Metadata.Storage.MapDefinition)
        then:
        thrown(ClassCastException)
    }

    def "Cast Map type to MapDefinition"() {
        setup:
        Metadata.Storage.Type<?> t = new Metadata.Storage.MapType(new Metadata.Storage.MapDefinition())
        when:
        Metadata.Storage.Type<Metadata.Storage.MapDefinition> act = t.cast(Metadata.Storage.MapDefinition)
        then:
        act == t
    }

    def "Cannot cast Map type to String"() {
        setup:
        Metadata.Storage.Type<?> t = new Metadata.Storage.MapType(new Metadata.Storage.MapDefinition())
        when:
        t.cast(String)
        then:
        thrown(ClassCastException)
    }

    def "Cast Double Map type to DoubleMapDefinition"() {
        setup:
        Metadata.Storage.Type<?> t = new Metadata.Storage.DoubleMapType(new Metadata.Storage.DoubleMapDefinition())
        when:
        Metadata.Storage.Type<Metadata.Storage.DoubleMapDefinition> act = t.cast(Metadata.Storage.DoubleMapDefinition)
        then:
        act == t
    }

    def "Cannot cast Double Map type to String"() {
        setup:
        Metadata.Storage.Type<?> t = new Metadata.Storage.DoubleMapType(new Metadata.Storage.DoubleMapDefinition())
        when:
        t.cast(String)
        then:
        thrown(ClassCastException)
    }
}
