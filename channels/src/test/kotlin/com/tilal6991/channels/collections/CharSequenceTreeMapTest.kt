package com.tilal6991.channels.collections

import org.assertj.core.api.assertThat
import org.junit.Test

class CharSequenceTreeMapTest {
    private val map = CharSequenceTreeMap<String>()

    @Test fun testInsert() {
        val old = map.put("key", "value")
        assertThat(old).isNull()

        assertThat(map["key"]).isEqualTo("value")
    }

    @Test fun testMultipleItemInsert() {
        val old = map.put("key", "value")
        val old1 = map.put("key1", "value1")
        val oldSubs = map.put("key1234", "value1234")
        val old2 = map.put("key2", "value2")
        val old3 = map.put("key3", "value3")
        assertThat(old).isNull()
        assertThat(old1).isNull()
        assertThat(oldSubs).isNull()
        assertThat(old2).isNull()
        assertThat(old3).isNull()

        assertThat(map["key"]).isEqualTo("value")
        assertThat(map["key1"]).isEqualTo("value1")
        assertThat(map["key1234"]).isEqualTo("value1234")
        assertThat(map["key2"]).isEqualTo("value2")
        assertThat(map["key3"]).isEqualTo("value3")
    }

    @Test fun testLongShort() {
        val old1 = map.put("keywhichisverylong", "value1")
        val old = map.put("k", "value")
        assertThat(old).isNull()
        assertThat(old1).isNull()

        assertThat(map["k"]).isEqualTo("value")
        assertThat(map["keywhichisverylong"]).isEqualTo("value1")
    }

    @Test fun testDuplicateInsert() {
        map.put("key", "value")

        val old = map.put("key", "updated")
        assertThat(map["key"]).isEqualTo("updated")
        assertThat(old).isEqualTo("value")
    }

    @Test fun testPrefixInsert() {
        map.put("key", "value")
        val old = map.put("k", "value2")

        assertThat(map["key"]).isEqualTo("value")

        assertThat(old).isNull()
        assertThat(map["k"]).isEqualTo("value2")
    }

    @Test fun testInsertCount() {
        assertThat(map.size).isEqualTo(0)

        map.put("key", "value")
        assertThat(map.size).isEqualTo(1)

        map.put("k", "val")
        assertThat(map.size).isEqualTo(2)

        map.put("key", "value")
        assertThat(map.size).isEqualTo(2)

        map.put("key", "updated")
        assertThat(map.size).isEqualTo(2)

        map.put("key1", "value")
        map.put("key123", "value1")
        map.put("key2", "value2")
        map.put("key12345", "value3")
        assertThat(map.size).isEqualTo(6)
    }

    @Test fun testRemoveCount() {
        assertThat(map.size).isEqualTo(0)

        map.put("key", "value")
        map.put("key1", "value")
        map.put("key2", "value")
        map.put("key3", "value")
        assertThat(map.size).isEqualTo(4)

        map.remove("key")
        assertThat(map.size).isEqualTo(3)

        map.remove("key")
        assertThat(map.size).isEqualTo(3)

        map.remove("key1")
        assertThat(map.size).isEqualTo(2)

        map.remove("key2")
        map.remove("key3")
        assertThat(map.size).isEqualTo(0)
    }

    @Test fun testRemove() {
        map.put("key", "value")

        val removed = map.remove("key")
        assertThat(map["key"]).isNull()

        assertThat(removed).isEqualTo("value")
    }
}