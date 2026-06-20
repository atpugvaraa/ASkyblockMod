// Milestone (a): the simplest possible FFM call.
// jextract will generate Java bindings for this `add` function.

public func add(_ a: Int64, _ b: Int64) -> Int64 {
    let result = a + b
    print("[ASM-SWIFT] add(\(a), \(b)) = \(result)")
    return result
}
