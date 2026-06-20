// swift-tools-version: 6.3
import PackageDescription

let package = Package(
    name: "ASMSwift",
    platforms: [.macOS(.v15)],
    products: [
        .library(name: "ASMCore", type: .dynamic, targets: ["ASMCore"])
    ],
    dependencies: [
        .package(name: "swift-java", path: "../../../../Swift/Packages/swift-java"),
        .package(path: "../../../../Swift/Packages/FabricKit"),
    ],
    targets: [
        .target(
            name: "ASMCore",
            dependencies: [
                .product(name: "SwiftJava", package: "swift-java"),
                .product(name: "SwiftRuntimeFunctions", package: "swift-java"),
                .product(name: "FabricKit", package: "FabricKit"),
            ],
            swiftSettings: [.swiftLanguageMode(.v5)],
            plugins: [
                .plugin(name: "JExtractSwiftPlugin", package: "swift-java")
            ]
        )
    ],
    swiftLanguageModes: [.v5]
)
