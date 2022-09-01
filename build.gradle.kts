plugins {
    id("fabric-loom")
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm").version(kotlinVersion)
}
base {
    val archivesBaseName: String by project
    archivesName.set(archivesBaseName)
}
val modVersion: String by project
version = modVersion
val mavenGroup: String by project
group = mavenGroup
repositories {
    maven("https://storage.googleapis.com/devan-maven/")//ARRP
    maven("https://server.bbkr.space/artifactory/libs-release") { name = "CottonMC" }//LibGui
    maven("https://github.com/Phoupraw/BowAPI/raw/1.18/publish")//BowAPI
    maven("D:/CCC/Documents/01_Programming/Fabric Mod/KotlinUtilMinecraft/publish")//KotlinUtilMinecraft
    maven("https://maven.siphalor.de/") { name = "Siphalor's Maven" }//nbtcrafting
    maven("https://maven.terraformersmc.com/") { name = "TerraformersMC" }//Trinkets
    maven("https://ladysnake.jfrog.io/artifactory/mods") { name = "Ladysnake Libs" }//Trinkets
    maven("https://modmaven.dev/") {//AE2
        name = "Modmaven"
        content { includeGroup("appeng") }// For Gradle 5.1 and above, limit it to just AE2
    }
    maven("https://maven.bai.lol/")//WTHIT
    maven("https://api.modrinth.com/maven")  // LazyDFU, Suggestion Tweaker
    maven("https://maven.terraformersmc.com/releases/") // Mod Menu, EMI
    maven("https://maven.shedaniel.me/") // Cloth Config, REI
    maven("https://mvn.devos.one/snapshots/") // Create, Porting Lib, Forge Tags, Milk Lib
    maven("https://cursemaven.com") // Forge Config API Port
    maven("https://maven.tterrag.com/") // Registrate and Flywheel
    maven("https://maven.cafeteria.dev/releases") // Fake Player API
    maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
    maven("https://jitpack.io/") // Mixin Extras, fabric ASM, nbtcrafting testing dependencies
    maven("D:/CCC/Documents/01_Programming/Fabric Mod/farmers-delight-fabric-1.18.2-0.2.1/publish")//本地编译的农夫乐事
}
dependencies {
    val minecraftVersion: String by project
    minecraft("com.mojang", "minecraft", minecraftVersion)
    val yarnMappings: String by project
    mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")
    val loaderVersion: String by project
    modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
    val fabricVersion: String by project
    modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion)
    val fabricKotlinVersion: String by project
    modImplementation("net.fabricmc", "fabric-language-kotlin", fabricKotlinVersion)
    
    val create: String by project
    modImplementation("com.simibubi.create", "create-fabric-${minecraftVersion}", create)
    val rei: String by project
    modCompileOnly("me.shedaniel", "RoughlyEnoughItems-api-fabric", rei)
    modCompileOnly("me.shedaniel", "RoughlyEnoughItems-fabric", rei)
    val suggestion_tweaker: String by project
    modLocalRuntime("maven.modrinth", "suggestion-tweaker", suggestion_tweaker)
    val lazydfu: String by project
    modLocalRuntime("maven.modrinth", "lazydfu", lazydfu)
    
    val arrp: String by project
    modImplementation("net.devtech", "arrp", arrp)
    val kt_util: String by project
    modImplementation(include("ph.mcmod", "KotlinUtilMinecraft", kt_util))
    val trinkets: String by project
    modImplementation("dev.emi", "trinkets", trinkets)
    val ae2: String by project
    modRuntimeOnly("appeng", "appliedenergistics2-fabric", ae2)
    val farmersdelight: String by project
    modImplementation("com.nhoryzon.mc", "farmers-delight-fabric", farmersdelight)
    
}
tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions { jvmTarget = javaVersion.toString() } }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        println(1)
        inputs.property("version", project.version)
        println(2)
        filesMatching("fabric.mod.json") {
            println(3)
            expand(mutableMapOf("version" to project.version))
            println(4)
        }
        println(5)
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
}
loom {
    accessWidenerPath.set(file("src/main/resources/c_storage.accesswidener"))
}