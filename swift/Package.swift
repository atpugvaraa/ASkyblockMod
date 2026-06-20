// swift-tools-version: 6.3
import CompilerPluginSupport
import PackageDescription

let package = Package(
    name: "ASMNative",
    platforms: [
        .macOS(.v15)
    ],
    products: [
        .library(
            name: "ASMCore",
            type: .dynamic,
            targets: ["ASMCore"]
        )
    ],
    dependencies: [
        .package(name: "swift-java", path: "../../../../Swift/Packages/swift-java")
    ],
    targets: [
        .target(
            name: "ASMCore",
            dependencies: [
                .product(name: "SwiftJava", package: "swift-java"),
                .product(name: "SwiftRuntimeFunctions", package: "swift-java"),
            ],
            exclude: [
                "swift-java.config"
            ],
            swiftSettings: [
                .swiftLanguageMode(.v5)
            ],
            plugins: [
                .plugin(name: "JExtractSwiftPlugin", package: "swift-java")
            ]
        )
    ]
)
