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


import kyc.KycWriter

/**
 * Bridge entry point.
 */
@CName("kyc_compile", "kyccmp")
fun compile(code: String, filename: String): String {
    return KycWriter.write(code, filename).hex()
}
