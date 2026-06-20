import FabricKit

/// Pure-Swift feature entrypoint. Kotlin calls this from the /asm command.
/// Returns a status string (no Swift errors cross the FFM boundary).
public func sendChatMessage(_ input: String) -> String {
    do {
        try Chat.send(input)
        return "[ASM] sent: \(input)"
    } catch {
        return "[ASM] failed: \(error)"
    }
}

public func openAsmScreen() -> String {
    do {
        try ScreenControl.openPauseMenu()
        return "[ASM] opened pause screen from Swift"
    } catch {
        return "[ASM] openAsmScreen failed: \(error)"
    }
}
