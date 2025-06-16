package me.blueb.residential

import me.blueb.residential.models.Config
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import kotlin.io.path.Path
import kotlin.time.measureTimedValue

class ResidentialConfig {
    companion object {
        val loader: YamlConfigurationLoader = YamlConfigurationLoader.builder()
            .path(Path("${Residential.instance.dataPath}/config.yml"))
            .defaultOptions { options ->
                options.serializers { builder ->
                    builder.registerAnnotatedObjects(objectMapperFactory())
                }
            }
            .build()

        val node: CommentedConfigurationNode = loader.load()
        var config: Config = node.get(Config::class.java) ?: Config()

        fun load() {
            config
        }
    }
}