import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    maven("https://server.bbkr.space/artifactory/libs-release") { name = "CottonMC" }//Lib Gui
    maven("https://github.com/Phoupraw/BowAPI/raw/1.18/publish")
    maven("D:\\CCC\\Documents\\01_Programming\\Fabric Mod\\KotlinUtilMinecraft\\publish")//KotlinUtilMinecraft
    maven("https://maven.siphalor.de/") { name = "Siphalor's Maven" }//nbtcrafting
    maven {//Trinkets
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {//Trinkets
        name = "Ladysnake Libs"
        url = uri("https://ladysnake.jfrog.io/artifactory/mods")
    }
    maven {//AE2
        name = "Modmaven"
        url = uri("https://modmaven.dev/")
        // For Gradle 5.1 and above, limit it to just AE2
        content {
            includeGroup("appeng")
        }
    }
    maven("https://maven.bai.lol/")//WTHIT
    maven("https://api.modrinth.com/maven")  // LazyDFU, Suggestion Tweaker
    maven("https://maven.terraformersmc.com/releases/") // Mod Menu, EMI
    maven("https://maven.shedaniel.me/") // Cloth Config, REI
    maven("https://mvn.devos.one/snapshots/") // Create, Porting Lib, Forge Tags, Milk Lib
    maven("https://cursemaven.com") // Forge Config API Port
    maven("https://maven.tterrag.com/") // Registrate and Flywheel
    maven("https://maven.cafeteria.dev") // Fake Player API
    maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
    maven("https://jitpack.io/") // Mixin Extras, fabric ASM, nbtcrafting testing dependencies
    maven("D:\\CCC\\Documents\\01_Programming\\Fabric Mod\\farmers-delight-fabric-1.18.2-0.2.1\\publish")
    
//    maven ("https://api.modrinth.com/maven") // LazyDFU, Suggestion Tweaker
//    maven ("https://maven.terraformersmc.com/releases/") // Mod Menu, EMI
//    maven ("https://maven.shedaniel.me/") // Cloth Config, REI
//    maven ("https://mvn.devos.one/snapshots/") // Create, Porting Lib, Forge Tags, Milk Lib
//    maven ("https://cursemaven.com") // Forge Config API Port
//    maven ("https://maven.tterrag.com/") // Registrate and Flywheel
//    maven ("https://maven.cafeteria.dev") // Fake Player API
//    maven ("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
//    maven ("https://jitpack.io/") // Mixin Extras, fabric ASM
//    maven ("https://dvs1.progwml6.com/files/maven/") // JEI
//    maven ("https://maven.parchmentmc.org") // Parchment mappings
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
    
    //my dependencies
    val arrpVersion: String by project
    modImplementation("net.devtech", "arrp", arrpVersion)
    modImplementation(include("ph.mcmod", "KotlinUtilMinecraft", "0.8.2"))
    modImplementation("dev.emi:trinkets:3.3.1")
    modRuntimeOnly("appeng:appliedenergistics2-fabric:11.1.3")//AE2
    modImplementation("com.nhoryzon.mc:farmers-delight-fabric:1.18.2-0.2.1")
    
    // create dev environment
    modLocalRuntime("maven.modrinth:lazydfu:0.1.2") // lazydfu - improves start times
    val suggestion_tweaker_version: String by project
    modLocalRuntime("maven.modrinth:suggestion-tweaker:$suggestion_tweaker_version") // suggestion tweaker - dev QOL, improves command suggestions
    
    // recipe viewers
//    val architectury_version: String by project
//    modRuntimeOnly("dev.architectury:architectury-fabric:${architectury_version}") // for REI
//    modRuntimeOnly("me.shedaniel.cloth:basic-math:0.6.0") // for REI
    val rei_version: String by project
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${rei_version}")// { isTransitive = false }
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-fabric:${rei_version}") //{ isTransitive = false }
//    val emi_version: String by project
//    modImplementation("dev.emi:emi:$emi_version") { isTransitive = false }
    
    // create setup
    val create_version: String by project
    modImplementation("com.simibubi.create:create-fabric-${minecraftVersion}:${create_version}")// { isTransitive = false }
//    val port_lib_version: String by project
//    val port_lib_hash: String by project
//    modImplementation("io.github.fabricators_of_create:Porting-Lib:${port_lib_version}+${minecraftVersion}-dev.${port_lib_hash}")
//    val forge_tags_version: String by project
//    modImplementation("me.alphamode:ForgeTags:${forge_tags_version}")
//    val night_config_core_version: String by project
//    modImplementation("com.electronwill.night-config:core:${night_config_core_version}")
//    val night_config_toml_version: String by project
//    modImplementation("com.electronwill.night-config:toml:${night_config_toml_version}")
//    val config_api_id: String by project
//    modImplementation("curse.maven:forge-config-api-port-fabric-547434:${config_api_id}")// { isTransitive = false }
//    val registrate_version: String by project
//    modImplementation("com.tterrag.registrate:Registrate:${registrate_version}")
//    val flywheel_version: String by project
//    modImplementation("com.jozufozu.flywheel:Flywheel-Fabric:${flywheel_version}")
//    val reach_entity_attributes_version: String by project
//    modImplementation("com.jamieswhiteshirt:reach-entity-attributes:${reach_entity_attributes_version}")
//    val fake_player_api_version: String by project
//    modImplementation("dev.cafeteria:fake-player-api:${fake_player_api_version}")
//    val milk_lib_version: String by project
//    modImplementation("io.github.tropheusj:milk-lib:${milk_lib_version}")
//    val jsr305_version: String by project
//    implementation("com.google.code.findbugs:jsr305:${jsr305_version}")
    
//    modImplementation("com.terraformersmc:modmenu:3.2.3")
}
tasks {
    val javaVersion = JavaVersion.VERSION_17
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion.toString() }
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
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