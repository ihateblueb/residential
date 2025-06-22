package me.blueb.residential

import me.blueb.residential.models.Config
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import kotlin.io.path.Path

class ResidentialConfig {
    companion object {
        lateinit var config: Config

        fun load() {
            val loader = YamlConfigurationLoader.builder()
                .path(Path("${Residential.instance.dataPath}/config.yml"))
                .defaultOptions { options ->
                    options.serializers { builder ->
                        builder.registerAnnotatedObjects(objectMapperFactory())
                    }
                }
                .build()

            val node = loader.load()
            config = node.get<Config>() ?: Config()

            println(config)
        }
    }
}