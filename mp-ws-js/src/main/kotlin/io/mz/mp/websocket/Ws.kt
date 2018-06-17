external fun require(module: String): dynamic

internal val ws = require("ws")

@Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")
internal inline fun new(clz: dynamic, param: dynamic): dynamic {
    return js("new clz(param)")
}