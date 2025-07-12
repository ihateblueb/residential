package site.remlit.blueb.residential

import site.remlit.blueb.residential.model.Config
import org.spongepowered.configurate.kotlin.extensions.get
import org.spongepowered.configurate.kotlin.objectMapperFactory
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import kotlin.io.path.Path

class Configuration {
    companion object {
        lateinit var config: Config

        fun load() {
            val loader = YamlConfigurationLoader.builder()
                .path(Path("${Residential.instance.dataFolder.path}/config.yml"))
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