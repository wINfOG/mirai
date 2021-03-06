/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

@file:Suppress("EXPERIMENTAL_API_USAGE")

package net.mamoe.mirai.contact

import net.mamoe.mirai.utils.*


/**
 * 只读联系人列表, lock-free 实现
 *
 * @see ContactList.asSequence
 */
@OptIn(MiraiInternalAPI::class)
@Suppress("unused")
class ContactList<C : Contact>(@MiraiInternalAPI val delegate: LockFreeLinkedList<C>) {
    /**
     * ID 列表的字符串表示.
     * 如:
     * ```
     * [123456, 321654, 123654]
     * ```
     */
    val idContentString: String get() = "[" + buildString { delegate.forEach { append(it.id).append(", ") } }.dropLast(2) + "]"

    operator fun get(id: Long): C = delegate[id]
    fun getOrNull(id: Long): C? = delegate.getOrNull(id)

    val size: Int get() = delegate.size
    operator fun contains(element: C): Boolean = delegate.contains(element)
    operator fun contains(id: Long): Boolean = delegate.getOrNull(id) != null
    fun containsAll(elements: Collection<C>): Boolean = elements.all { contains(it) }
    fun isEmpty(): Boolean = delegate.isEmpty()
    inline fun forEach(block: (C) -> Unit) = delegate.forEach(block)
    fun first(): C {
        forEach { return it }
        throw NoSuchElementException()
    }

    fun firstOrNull(): C? {
        forEach { return it }
        return null
    }

    override fun toString(): String = delegate.asSequence().joinToString(separator = ", ", prefix = "ContactList(", postfix = ")")
}

operator fun <C : Contact> LockFreeLinkedList<C>.get(id: Long): C {
    forEach { if (it.id == id) return it }
    throw NoSuchElementException("No such contact: $id")
}

fun <C : Contact> LockFreeLinkedList<C>.getOrNull(id: Long): C? {
    forEach { if (it.id == id) return it }
    return null
}

inline fun <C : Contact> LockFreeLinkedList<C>.filteringGetOrNull(filter: (C) -> Boolean): C? {
    forEach { if (filter(it)) return it }
    return null
}


/**
 * Collect all the elements into a [MutableList] then cast it as a [List]
 */
fun <E : Contact> ContactList<E>.toList(): List<E> = toMutableList()

/**
 * Collect all the elements into a [MutableList].
 */
@OptIn(MiraiInternalAPI::class)
fun <E : Contact> ContactList<E>.toMutableList(): MutableList<E> = this.delegate.toMutableList()

/**
 * Collect all the elements into a [MutableSet] then cast it as a [Set]
 */
fun <E : Contact> ContactList<E>.toSet(): Set<E> = toMutableSet()

/**
 * Collect all the elements into a [MutableSet].
 */
@OptIn(MiraiInternalAPI::class)
fun <E : Contact> ContactList<E>.toMutableSet(): MutableSet<E> = this.delegate.toMutableSet()

/**
 * Builds a [Sequence] containing all the elements in [this] in the same order.
 *
 * Note that the sequence is dynamic, that is, elements are yielded atomically only when it is required
 */
@OptIn(MiraiInternalAPI::class)
fun <E : Contact> ContactList<E>.asSequence(): Sequence<E> {
    return this.delegate.asSequence()
}