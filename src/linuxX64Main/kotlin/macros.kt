/*
 * This file is part of kython-bridge.
 *
 * kython-bridge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * kython-bridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with kython.  If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("FunctionName")

import cpython.*
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readBytes

fun PyError(msg: String? = null): Nothing {
    PyErr_Print()
    if (msg != null) error(msg)
    else error("Python error!")
}

/**
 * Makes sure a PyObject is a PyUnicodeObject.
 */
fun PyUnicode_Check(obb: PyObject): Boolean {
    val type = obb.ob_type!!
    val flags = PyType_GetFlags(type)
    val i = flags and Py_TPFLAGS_UNICODE_SUBCLASS
    return i != 0UL
}

fun PyBytes_Check(obb: PyObject): Boolean {
    val flags = PyType_GetFlags(obb.ob_type)
    val i = flags and Py_TPFLAGS_BYTES_SUBCLASS
    return i != 0UL
}

/**
 * Gets a [ByteArray] from a [PyObject].
 */
fun PyBytes_AsByteArray(obb: PyObject): ByteArray {
    val ptr = obb.ptr
    val size = PyBytes_Size(ptr)
    val bp = PyBytes_AsString(ptr)!!
    val ba = bp.readBytes(size.toInt())
    return ba
}

fun PyUnicode_AsByteArray(obb: PyObject): ByteArray {
    val ptr = obb.ptr
    val encoded = PyUnicode_AsEncodedString(ptr, "utf-8", null)
        ?: PyError("Failed to encode string")
    val ba = PyBytes_AsByteArray(encoded.pointed)

    // must decref, new reference
    Py_DecRef(encoded)

    return ba
}

fun PyTuple_Check(obb: PyObject): Boolean {
    val flags = PyType_GetFlags(obb.ob_type)
    val i = flags and Py_TPFLAGS_TUPLE_SUBCLASS
    return i != 0UL
}

fun PyList_Check(obb: PyObject): Boolean {
    val flags = PyType_GetFlags(obb.ob_type)
    val i = flags and Py_TPFLAGS_LIST_SUBCLASS
    return i != 0UL
}

fun PyLong_Check(obb: PyObject): Boolean {
    val flags = PyType_GetFlags(obb.ob_type)
    val i = flags and Py_TPFLAGS_LONG_SUBCLASS
    return i != 0UL
}

fun PyFloat_Check(obb: PyObject): Boolean {
    return PyType_IsSubtype(obb.ob_type!!, PyFloat_Type.ptr) != 0
}

fun PyCode_Check(obb: PyObject): Boolean {
    return obb.ob_type!!.pointed == PyCode_Type
}

fun PySet_Check(obb: PyObject): Boolean {
    val type = obb.ob_type!!

    // frozenset special handling
    if (type == PyFrozenSet_Type.ptr) return true
    // just normal sets
    return PyType_IsSubtype(obb.ob_type!!, PySet_Type.ptr) != 0
}

fun PyDict_Check(obb: PyObject): Boolean {
    val flags = PyType_GetFlags(obb.ob_type)
    val i = flags and Py_TPFLAGS_DICT_SUBCLASS
    return i != 0UL
}
